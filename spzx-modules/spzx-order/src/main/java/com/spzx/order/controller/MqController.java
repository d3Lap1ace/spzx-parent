package com.spzx.order.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.configure.DeadLetterMqConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mq接口管理")
@RestController
@RequestMapping("/mq")
public class MqController extends BaseController {

    @Autowired
    private RabbitService rabbitService;

    @GetMapping("/sendMessage")
    public AjaxResult sendMessage() {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_TEST, "hello");
        return success();
    }

    /**
     * 消息发送延迟消息：基于死信实现
     */
    @Operation(summary = "发送延迟消息：基于死信实现")
    @GetMapping("/sendDeadLetterMsg")
    public AjaxResult sendDeadLetterMsg() {
        rabbitService.sendMessage(DeadLetterMqConfig.exchange_dead,DeadLetterMqConfig.routing_dead_1,"pingMessage");
        return success();
    }

}