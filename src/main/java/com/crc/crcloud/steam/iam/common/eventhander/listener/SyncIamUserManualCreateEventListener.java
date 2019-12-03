package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.user.IamUserCreateWithPasswordDTO;
import com.crc.crcloud.steam.iam.model.event.IamUserManualCreateEvent;
import com.crc.crcloud.steam.iam.model.feign.user.UserDTO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 手动创建用户-同步到老服务的iam-server
 * @author LiuYang
 * @date 2019/11/22
 */
@Slf4j
@Component
public class SyncIamUserManualCreateEventListener implements ApplicationListener<IamUserManualCreateEvent> {
    @Autowired
    private IamServiceClient iamServiceClient;
    @Autowired
    private IamOrganizationService organizationService;
    @Autowired
    private IamUserService iamUserService;

    public SyncIamUserManualCreateEventListener() {
        log.info("已注册手动创建用户事件-同步用户到老行云");
    }

    @Override
    public void onApplicationEvent(IamUserManualCreateEvent event) {
        for (IamUserCreateWithPasswordDTO userCreateWithPasswordDTO : event.getUsers()) {
            @NotNull IamUserDTO iamUser = userCreateWithPasswordDTO.getUser();
            iamUserService.getHashPassword(iamUser.getId()).ifPresent(iamUser::setHashPassword);
            UserDTO userDTO = new UserDTO();
            BeanUtil.copyProperties(iamUser, userDTO);
            userDTO.setPassword(iamUser.getHashPassword());
            final String logTitle = StrUtil.format("手动创建组织成员[{}]同步到iam-server", iamUser.getLoginName());
            log.info(logTitle);
            Optional<IamOrganizationDTO> firstOrg = organizationService.getUserOrganizations(iamUser.getId()).stream().findFirst();
            if (firstOrg.isPresent()) {
                try {
                    userDTO.setOrganizationId(firstOrg.get().getId());
                    ResponseEntity<UserDTO> responseEntity = iamServiceClient.syncSteamUser(userDTO);
                    Predicate<ResponseEntity<UserDTO>> isSuccess = t -> t.getStatusCode().is2xxSuccessful();
                    isSuccess = isSuccess.and(t -> JSONUtil.parseObj(t.getBody()).containsKey(EntityUtil.getSimpleFieldToCamelCase(UserDTO::getId)));
                    isSuccess = isSuccess.and(t -> JSONUtil.parseObj(t.getBody()).containsKey(EntityUtil.getSimpleFieldToCamelCase(UserDTO::getLoginName)));
                    if (isSuccess.test(responseEntity)) {
                        log.error("{};同步成功: {}", logTitle, JSONUtil.toJsonStr(responseEntity.getBody()));
                    } else {
                        log.error("{};同步失败: {}", logTitle, JSONUtil.toJsonStr(responseEntity.getBody()));
                    }
                } catch (Exception ex) {
                    log.error("{};同步异常: {}", logTitle, ex.getMessage(), ex);
                }
            } else {
                log.error("{};没有发现组织", logTitle);
            }
        }
    }
}
