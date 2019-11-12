package com.crc.crcloud.steam.iam;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = {"com.crc.crcloud.steam.iam.dao"})
@EnableEurekaClient
@EnableChoerodonResourceServer
@EnableFeignClients({"io.choerodon", "com.crc.crcloud.steam.iam.api.feign"})
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
public class SteamIamApplication {

    public static void main(String[] args) {

        SpringApplication.run(SteamIamApplication.class, args);
    }
}

