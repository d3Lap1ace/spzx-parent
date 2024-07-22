package com.spzx.order.receiver;


import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.order.configure.DeadLetterMqConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TestReceiver {

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_TEST, durable = "true"),
            value = @Queue(value = MqConst.QUEUE_TEST, durable = "true"),
            key = MqConst.ROUTING_TEST
    ))
    public void test(String content, Message message) {
        System.out.println("获取发送过来的消息："+content);
        //message.getBody()
    }

    /**
     * 监听延迟消息
     * @param content
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = {DeadLetterMqConfig.queue_dead_2})
    public void getDeadLetterMsg(String content, Message message, Channel channel) {
        log.info("dead msg :{}",content);
        System.out.println("content = " + content);
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}