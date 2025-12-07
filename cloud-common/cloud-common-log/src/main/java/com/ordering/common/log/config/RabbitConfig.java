package com.ordering.common.log.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 定义日志队列名称
    public static final String LOG_QUEUE = "sys.log.queue";

    @Bean
    public Queue logQueue() {
        // 持久化队列
        return QueueBuilder.durable(LOG_QUEUE).build();
    }
}