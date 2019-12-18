package com.crc.crcloud.steam.iam.common.utils;

import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.IamProjectMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.IamProject;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class AssertHelper {

    /**
     * 检查组织是否存在
     * @param organizationId
     * @return
     */
    public IamOrganization organizationNotExisted(Long organizationId) {
        IamOrganizationMapper iamOrganizationMapper = ApplicationContextHelper.getContext().getBean(IamOrganizationMapper.class);
        IamOrganization organization = iamOrganizationMapper.selectById(organizationId);
        if (ObjectUtils.isEmpty(organization)) {
            throw new IamAppCommException("error.steam-iamOrganization.notFound", organizationId);
        }
        return organization;
    }

    /**
     * 检查项目是否存在
     * @param projectId
     * @return
     */
    public IamProject projectNotExisted(Long projectId) {
        IamProjectMapper iamProjectMapper = ApplicationContextHelper.getContext().getBean(IamProjectMapper.class);
        IamProject project = iamProjectMapper.selectById(projectId);
        if (ObjectUtils.isEmpty(project)) {
            throw new IamAppCommException("error.steam-iamProject.not.exist", projectId);
        }
        return project;
    }

    /**
     * 检查应用是否存在
     * @param applicationId
     * @return
     */
    public IamApplication applicationNotExisted(Long applicationId){
        IamApplicationMapper iamApplicationMapper = ApplicationContextHelper.getContext().getBean(IamApplicationMapper.class);
        IamApplication iamApplication = iamApplicationMapper.selectById(applicationId);
        if (ObjectUtils.isEmpty(iamApplication)) {
            throw new CommonException("error.steam-iamApplication.not.exist");
        }
        return iamApplication;
    }

    /**
     * 检查版本号是否存在
     * @param objectVersionNumber
     */
    public void objectVersionNumberNotNull(Long objectVersionNumber){
        if (ObjectUtils.isEmpty(objectVersionNumber)) {
            throw new CommonException("error.steam-iamObjectVersionNumber.isNull");
        }
    }
}
