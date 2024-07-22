package com.spzx.order.configure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 22/7/2024 16:10 周一
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Configuration
public class DeadLetterMqConfig{
    public static final String exchange_dead = "exchange.dead";
    public static final String routing_dead_1 = "routing.dead.1";
    public static final String routing_dead_2 = "routing.dead.2";
    public static final String queue_dead_1 = "queue.dead.1";
    public static final String queue_dead_2 = "queue.dead.2";

    // 定义交换机
    @Bean
    public DirectExchange exchangeDead(){
        return new DirectExchange(exchange_dead,true,false,null);
    }

    // 定义消息队列1
    @Bean
    public Queue queue1(){
        // 如果队列一 出现问题,则通过参数转到exchange_dead,routing_dead_2 上
        HashMap<String, Object> map = new HashMap<>();
        // 参数绑定 此处的key 固定值 不能随便写
        map.put("x-dead-letter-exchange", exchange_dead);
        map.put("x-dead-letter-routing-key", routing_dead_2);
        // 设置延迟时间
        map.put("x-message-ttl", 10*1000);
        // 队列名称,是否支持持久化,是否独享,排外
        return new Queue(queue_dead_1,true,false,false,map);
    }

    // 将队列1绑定到交换机上
    @Bean
    public Binding bingding(){
        // 将队列一 通过routing_dead_1 key 绑定到exchange_dead  交换机上
        return BindingBuilder.bind(queue1()).to(exchangeDead()).with(routing_dead_1);
    }

    // 定义普通消息队列2
    public Queue queue2(){
        return new Queue(queue_dead_2,true,false,false,null);
    }

    // 设置队列二的绑定规则
    @Bean
    public Binding binding2() {
        // 将队列二通过routing_dead_2 key 绑定到exchange_dead交换机上！
        return BindingBuilder.bind(queue2()).to(exchangeDead()).with(routing_dead_2);
    }

}
