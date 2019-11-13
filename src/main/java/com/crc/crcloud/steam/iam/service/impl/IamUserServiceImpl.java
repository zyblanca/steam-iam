package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamUserMapper;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.convertor.ConvertHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Validated
@Slf4j
@Service
public class IamUserServiceImpl implements IamUserService {

	@Autowired
	private IamUserMapper iamUserMapper;
	/**线程安全*/
	private final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
	@Override
	public @NotNull IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo, @NotEmpty Set<Long> organizationIds) {
		//只能以字母和数字开头，且长度不能少于2，内容可以包含字母数字.-
		Predicate<String> matchLoginName = loginName -> ReUtil.isMatch("^[a-zA-Z0-9][a-zA-Z0-9.-]+$", loginName);
		//不能以. .git .atom 结尾
		matchLoginName = matchLoginName.and(loginName -> !StrUtil.endWithAny(".", ".git", ".atom"));
		if (matchLoginName.negate().test(vo.getLoginName())) {
			throw new IamAppCommException("user.loginName.content");
		}
		if (getByLoginName(vo.getLoginName()).isPresent()) {
			throw new IamAppCommException("user.loginName.exist");
		}
		if (getByEmail(vo.getEmail()).isPresent()) {
			throw new IamAppCommException("user.email.exist");
		}
		IamUser entity = new IamUser();
		BeanUtil.copyProperties(vo, entity);
		initUser(entity);
		iamUserMapper.insert(entity);
		iamUserMapper.fillHashPassword(entity.getId(), ENCODER.encode(vo.getPassword()));
		log.info("手动添加用户[{}],期望属于组织[{}]", vo.getRealName(), CollUtil.join(organizationIds, ","));
		return ConvertHelper.convert(entity, IamUserDTO.class);
	}

	/**
	 * 初始化用户的基本属性,如果属性为null，则进行初始化
	 * <p>是否管理员{@link IamUser#getIsAdmin()} ()}：false</p>
	 * <p>国际电话区号{@link IamUser#getInternationalTelCode()} ()}：+86</p>
	 * <p>语言{@link IamUser#getLanguage()}：zh_CN</p>
	 * <p>时区{@link IamUser#getTimeZone()}：CTT</p>
	 * <p>是否启用{@link IamUser#getIsEnabled()}：true</p>
	 * <p>是否锁定账户{@link IamUser#getIsLocked()}：false</p>
	 * <p>是否ldap来源{@link IamUser#getIsLdap()}：false</p>
	 * @param entity 属性
	 */
	private void initUser(@NotNull IamUser entity) {
		IamUser init = IamUser.builder()
				.isAdmin(false)
				.internationalTelCode("+86")
				.language("zh_CN")
				.timeZone("CTT")
				.isEnabled(true)
				.isLocked(false)
				.isLdap(false)
				.build();
		Map<String, Object> initField = BeanUtil.beanToMap(init, false, true);
		BeanDesc beanDesc = BeanUtil.getBeanDesc(IamUser.class);
		initField.forEach((k, v) -> {
			BeanDesc.PropDesc prop = beanDesc.getProp(k);
			Object value = prop.getValue(entity);
			if (Objects.isNull(value)) {
				prop.setValue(entity, v);
			}
		});
	}

	public Optional<IamUserDTO> getByLoginName(@NotBlank String loginName) {
		return getOne(t -> t.eq(IamUser::getLoginName, loginName));
	}

	public Optional<IamUserDTO> getByEmail(@NotBlank String email) {
		return getOne(t -> t.eq(IamUser::getEmail, email));
	}

	/**
	 * 根据条件获取第一个
	 * @param consumer 条件
	 * @return 复合条件的第一个用户
	 */
	private Optional<IamUserDTO> getOne(@NotNull Consumer<LambdaQueryWrapper<IamUser>> consumer) {
		LambdaQueryWrapper<IamUser> queryWrapper = Wrappers.<IamUser>lambdaQuery();
		consumer.accept(queryWrapper);
		return iamUserMapper.selectList(queryWrapper)
				.stream()
				.findFirst()
				.map(t -> ConvertHelper.convert(t, IamUserDTO.class));
	}


}
