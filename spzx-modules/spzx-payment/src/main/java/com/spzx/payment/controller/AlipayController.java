package com.spzx.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.payment.configure.AlipayConfig;
import com.spzx.payment.service.IAlipayService;
import com.spzx.payment.service.IPaymentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 23/7/2024 14:35 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Controller
@RequestMapping("/alipay")
public class AlipayController extends BaseController {
    @Autowired
    private IAlipayService alipayService;
    @Autowired
    private IPaymentInfoService paymentInfoService;

    @Operation(summary = "支付宝下单")
    @RequiresLogin
    @RequestMapping("submitAlipay/{orderNo}")
    @ResponseBody
    public AjaxResult submitAlipay(@PathVariable(value = "orderNo") String orderNo){
        String form = alipayService.submitAlipay(orderNo);
        return success(form);
    }

    /**
     * 支付宝同步回调
     * @return
     */
    @Operation(summary = "支付宝同步回调")
    @GetMapping("callback/return")
    public String callBack() {
        // 同步回调给用户展示信息
        return "redirect:" + AlipayConfig.return_order_url;
    }

    @Operation(summary = "支付宝异步回调")
    @RequestMapping("callback/notify")
    @ResponseBody
    public String alipayNotify(@RequestParam Map<String, String> paramMap, HttpServletRequest request) {
        log.info("AlipayController...alipayNotify方法执行了...");
        //这个方法是支付成功之后，支付宝异步回调，支付宝传递值
        //校验数据是否正确
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramMap,AlipayConfig.alipay_public_key,AlipayConfig.charset,AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        // 交易状态
        String trade_status = paramMap.get("trade_status");
        if(signVerified){
            // 判断trade_status,支付成功
            if("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)){
                // 正常的支付成功,更新交易记录状态
                paymentInfoService.updatePaymentStatus(paramMap,"2");
                return "success" ;
            }
        }else {
            return "failure" ;
        }
        return "failure" ;
    }


}
