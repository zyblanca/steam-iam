package com.crc.crcloud.steam.iam.web;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.crc.crcloud.steam.iam.api.feign.FileFeignClient;
import com.crc.crcloud.steam.iam.common.constant.IamConstant;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.common.utils.SearchUtil;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.dto.organization.IamOrganizationWithProjectCountDTO;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationCreateRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationPageResponseVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationUpdateRequestVO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


/**
 * ????????????
 * <p>???????????? </p>
 * <p>?????????????????? </p>
 * <p>?????????????????? </p>
 *
 * @author LiuYang
 */
@Validated
@Api("")
@RestController
@RequestMapping(value = "/v1/organizations")
@Slf4j
public class IamOrganizationController {

    @Autowired
    private IamOrganizationService iamOrganizationService;
    @Autowired
    private FileFeignClient fileFeignClient;

    /**
     * ??????????????????
     *
     * @return ??????????????????????????????
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "?????????????????????")
    @PutMapping(value = "/{organization_id}")
    public ResponseEntity<IamOrganizationVO> updateBySite(@PathVariable(name = "organization_id") Long id,
                                                          @RequestBody @Valid IamOrganizationUpdateRequestVO vo) {
        return updateByLevel(id, vo, ResourceLevel.SITE);
    }

    /**
     * ??????????????????
     *
     * @return ??????????????????????????????
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "?????????????????????")
    @PutMapping(value = "/{organization_id}/organization_level")
    public ResponseEntity<IamOrganizationVO> updateByOrganization(@PathVariable(name = "organization_id") Long id,
                                                                  @RequestBody @Valid IamOrganizationUpdateRequestVO vo) {
        return updateByLevel(id, vo, ResourceLevel.ORGANIZATION);
    }

    private ResponseEntity<IamOrganizationVO> updateByLevel(Long id, IamOrganizationUpdateRequestVO vo, ResourceLevel level) {
        //??????????????????
        vo.setIsEnabled(vo.getIsEnabled());
        Map<ResourceLevel, BiFunction<Long, IamOrganizationUpdateRequestVO, IamOrganizationDTO>> handler = new HashMap<>(3);
        handler.put(ResourceLevel.SITE, iamOrganizationService::updateBySite);
        handler.put(ResourceLevel.ORGANIZATION, iamOrganizationService::updateByOrganization);
        if (handler.containsKey(level)) {
            IamOrganizationDTO iamOrganization = handler.get(level).apply(id, vo);
            return new ResponseEntity<>(ConvertHelper.convert(iamOrganization, IamOrganizationVO.class));
        }
        return new ResponseEntity<>();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "????????????")
    @PutMapping(value = "/{organization_id}/enable")
    public ResponseEntity<IamOrganizationVO> enableOrganization(@PathVariable(name = "organization_id") Long id) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        iamOrganizationService.toggleEnable(id, Boolean.TRUE, userId);
        return new ResponseEntity<>(ConvertHelper.convert(iamOrganizationService.getAndThrow(id), IamOrganizationVO.class));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "????????????")
    @PutMapping(value = "/{organization_id}/disable")
    public ResponseEntity<IamOrganizationVO> disableOrganization(@PathVariable(name = "organization_id") Long id) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        iamOrganizationService.toggleEnable(id, Boolean.FALSE, userId);
        return new ResponseEntity<>(ConvertHelper.convert(iamOrganizationService.getAndThrow(id), IamOrganizationVO.class));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "??????????????????")
    @PostMapping(value = "upload/image")
    public ResponseEntity<String> uploadOrganizationImageUrl(@RequestParam(value = "file") @NotNull MultipartFile file) {
        BigDecimal mb = NumberUtil.div(BigDecimal.valueOf(file.getSize()), BigDecimal.valueOf(1024 * 1024L), 2, RoundingMode.HALF_UP);
        log.info("????????????????????????: {} size:{}/m|{}/bytes", file.getOriginalFilename(), mb.toPlainString(), file.getSize());
        if (NumberUtil.isGreater(mb, BigDecimal.ONE)) {
            throw new IamAppCommException("organization.image.file.size");
        }
        final String extName = FileUtil.extName(file.getOriginalFilename());
        boolean matchExtName = CollUtil.newHashSet("jpg", "jpeg", "png").stream().anyMatch(supportExtName -> supportExtName.equalsIgnoreCase(extName));
        if (!matchExtName) {
            throw new IamAppCommException("organization.image.file.illegal");
        }
        final String fileName = StrUtil.format("file_{}.{}", IdWorker.getIdStr(), extName);
        String imageUrl = fileFeignClient.uploadFile(IamConstant.BUCKET_NAME, fileName, file).getBody();
        log.info("upload file imageUrl: {}", imageUrl);
        return new ResponseEntity<>(imageUrl);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "????????????")
    @PostMapping(value = "")
    public ResponseEntity<IamOrganizationVO> create(@RequestBody @Valid IamOrganizationCreateRequestVO vo) {
        IamOrganizationDTO organization = iamOrganizationService.create(vo);
        return new ResponseEntity<>(ConvertHelper.convert(organization, IamOrganizationVO.class));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "????????????(??????)")
    @PostMapping(value = "page")
    public ResponseEntity<IPage<IamOrganizationPageResponseVO>> page(@RequestBody IamOrganizationPageRequestVO vo) {
        //?????????????????? ??????
        vo.setCode(SearchUtil.likeParam(vo.getCode()));
        vo.setName(SearchUtil.likeParam(vo.getName()));
        @NotNull IPage<IamOrganizationWithProjectCountDTO> pageResult = iamOrganizationService.page(vo);
        return new ResponseEntity<>(pageResult.convert(t -> {
            IamOrganizationPageResponseVO responseVO = new IamOrganizationPageResponseVO();
            BeanUtil.copyProperties(t, responseVO);
            return responseVO;
        }));
    }

    /**
     * ????????????id????????????
     *
     * @param id ?????????????????????id???
     * @return ????????????
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "?????????????????????id????????????")
    @GetMapping(value = "/{organization_id}")
    public ResponseEntity<IamOrganizationVO> query(@PathVariable(name = "organization_id") Long id) {
        return new ResponseEntity<>(CopyUtil.copy(iamOrganizationService.getAndThrow(id), IamOrganizationVO.class));
    }


}
