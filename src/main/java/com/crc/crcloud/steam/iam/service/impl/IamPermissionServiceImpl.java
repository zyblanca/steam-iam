package com.crc.crcloud.steam.iam.service.impl;


import com.crc.crcloud.steam.iam.dao.IamPermissionMapper;
import com.crc.crcloud.steam.iam.service.IamPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author
 * @Description
 * @Date 2019-11-29
 */
@Service
public class IamPermissionServiceImpl implements IamPermissionService {

    @Autowired
    private IamPermissionMapper iamPermissionMapper;

}
