package com.ordering.auth;

import com.ordering.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 认证授权中心
 * 
 * @author cloud
 */
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CloudAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CloudAuthApplication.class, args);
        System.out.println("Account认证授权中心启动成功");
    }
}
