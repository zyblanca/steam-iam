package com.crc.crcloud.steam.iam.service.impl;

import com.crc.crcloud.steam.iam.dao.IamApplicationExplorationMapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.service.IamApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IamApplicationServiceImpl implements IamApplicationService {

    @Autowired
    private IamApplicationMapper iamApplicationMapper;
    @Autowired
    private IamApplicationExplorationMapper iamApplicationExplorationMapper;

}
