package com.spzx.product.receive;

import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.product.service.IProductService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 23/7/2024 10:18 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */


@Slf4j
@Component
public class ProductReceiver {

    @Autowired
    private IProductService productService;

    /**
     * 解锁库存
     * @param orderNo 订单号
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = MqConst.EXCHANGE_PRODUCT,durable = "true"),
            value = @Queue(value = MqConst.QUEUE_UNLOCK, durable = "true"),
            key = {MqConst.ROUTING_UNLOCK}))
    public void unlock(String orderNo, Message message, Channel channel) {
        // 业务处理
        if(StringUtils.isNotEmpty(orderNo)){
            productService.unlock(orderNo);
        }
        // 手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 扣减库存
     * @param orderNo  订单号
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = MqConst.EXCHANGE_PRODUCT,durable = "true"),
    value = @Queue(value = MqConst.QUEUE_MINUS, durable = "true"),
            key = {MqConst.ROUTING_MINUS}))
    public void minus(String orderNo, Channel channel, Message message){
        if(StringUtils.isNotEmpty(orderNo)){
            // 扣减库存
            productService.minus(orderNo);
        }
        // 应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
