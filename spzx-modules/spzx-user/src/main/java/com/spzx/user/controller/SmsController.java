package com.spzx.user.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.user.service.ISmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 17/7/2024 11:51 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Tag(name = "短信接口")
@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Autowired
    private ISmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Operation(summary = "获取手机验证码")
    @GetMapping("/sendCode/{phone}")
    public AjaxResult sendcode(@PathVariable("phone") String phone){

        // 根据手机号查询redis 如果有 则直接返回
        String recode = (String) redisTemplate.opsForValue().get("code:" + phone);
        if(StringUtils.hasText(recode)){
            return success();
        }

        // 生成验证码格式
        String code = new DecimalFormat("00000").format(new Random().nextInt(10000));

        // 存入hashMap 作为验证
        HashMap<String, String> map = new HashMap<>();
        map.put("code",code);

        Boolean flag = smsService.send(phone,map);
        log.info("发送成功");

        // 存入redis
        if(flag){
            redisTemplate.opsForValue().set("code:",code,5, TimeUnit.MINUTES);
        }
        return success();
    }
}
