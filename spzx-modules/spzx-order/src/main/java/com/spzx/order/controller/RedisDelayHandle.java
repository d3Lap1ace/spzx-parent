package com.spzx.order.controller;

import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.order.service.IOrderInfoService;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 22/7/2024 18:29 周一
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Component
public class RedisDelayHandle {

    @Autowired
    private IOrderInfoService orderInfoService;

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void listener() {
        new Thread(()->{
            while (true) {
                try {
                    // 创建阻塞队列
                    RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(MqConst.ROUTING_CANCEL_ORDER);
                    String orderId = (String) blockingDeque.take();
                    if(!StringUtils.hasText(orderId)){
                        // 调用方法取消订单
                        orderInfoService.processCloseOrder(Long.parseLong(orderId));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
