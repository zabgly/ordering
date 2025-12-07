package com.ordering;

import com.ordering.common.security.annotation.EnableCustomConfig;
import com.ordering.common.security.annotation.EnableRyFeignClients;
import com.ordering.common.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 旅游景点服务
 *
 * 旅游景点，旅游攻略，评论
 * 
 * @author cloud
 */
@EnableCustomConfig
@EnableRyFeignClients
@MapperScan("com.ordering.**.mapper")
@EnableCustomSwagger2
@SpringBootApplication
public class CloudUmsApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CloudUmsApplication.class, args);
        System.out.println("用户服务模块启动成功");
           
    }
}
