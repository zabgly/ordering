package com.ordering.system;

import com.ordering.common.security.annotation.EnableCustomConfig;
import com.ordering.common.security.annotation.EnableRyFeignClients;
import com.ordering.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统模块
 * 
 * @author cloud
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class CloudSystemApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CloudSystemApplication.class, args);
        System.out.println("User系统管理模块启动成功");
    }
}
