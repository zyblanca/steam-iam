package com.crc.crcloud.steam.iam.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class LdapThreadPoolConfig {


    @Bean
    @Qualifier("ldap-executor")
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("ldap-executor");
        executor.setMaxPoolSize(4);
        executor.setCorePoolSize(3);
        //最大队列
        executor.setQueueCapacity(1000);
        return executor;
    }

}
