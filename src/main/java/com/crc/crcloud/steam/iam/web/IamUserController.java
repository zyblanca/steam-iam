package com.crc.crcloud.steam.iam.web;


import com.crc.crcloud.steam.iam.service.IamUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
* @Author:
* @Date: 2019-11-12
* @Description:
*/
@Api("")
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/iam_user")
public class IamUserController {

    @Autowired
    private IamUserService iamUserService;


}
