package com.ordering.system.consumer;

import com.ordering.common.log.config.RabbitConfig;
import com.ordering.system.api.domain.SysOperLog;
import com.ordering.system.service.ISysOperLogService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogConsumer {

    @Autowired
    private ISysOperLogService sysOperLogService;

    // 监听日志队列，消费消息并保存到数据库
    @RabbitListener(queues = RabbitConfig.LOG_QUEUE)
    public void handleLog(SysOperLog sysOperLog) {
        sysOperLogService.insertOperlog(sysOperLog);
    }
}