package com.crc.crcloud.steam.iam.common.utils;

import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.IamProjectMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.IamProject;
import io.choerodon.core.convertor.ApplicationContextHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class AssertHelper {

    public IamOrganization organizationNotExisted(Long organizationId) {
        IamOrganizationMapper iamOrganizationMapper = ApplicationContextHelper.getContext().getBean(IamOrganizationMapper.class);
        IamOrganization organization = iamOrganizationMapper.selectById(organizationId);
        if (ObjectUtils.isEmpty(organization)) {
            throw new IamAppCommException("error.iamOrganization.notFound", organizationId);
        }
        return organization;
    }

    public IamProject projectNotExisted(Long projectId) {
        IamProjectMapper iamProjectMapper = ApplicationContextHelper.getContext().getBean(IamProjectMapper.class);
        IamProject project = iamProjectMapper.selectById(projectId);
        if (ObjectUtils.isEmpty(project)) {
            throw new IamAppCommException("error.project.not.exist", projectId);
        }
        return project;
    }
}
