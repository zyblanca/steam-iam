package com.crc.crcloud.steam.iam.service.impl;


import com.crc.crcloud.steam.iam.dao.IamRolePermissionMapper;
import com.crc.crcloud.steam.iam.service.IamRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author
 * @Description
 * @Date 2019-11-29
 */
@Service
public class IamRolePermissionServiceImpl implements IamRolePermissionService {

    @Autowired
    private IamRolePermissionMapper iamRolePermissionMapper;


}
