package com.spzx.payment.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeWapMergePayRequest;
import com.spzx.payment.configure.AlipayConfig;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.service.IAlipayService;
import com.spzx.payment.service.IPaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 23/7/2024 14:38 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Service
public class AlipayServiceImpl implements IAlipayService {

    @Autowired
    private IPaymentInfoService paymentInfoService;
    @Autowired
    private AlipayClient alipayClient;

    @Override
    public String submitAlipay(String orderNo) {
        // 保存支付记录
        PaymentInfo paymentInfo =  paymentInfoService.savePaymentInfo(orderNo);
        // 生成二维码
        AlipayTradeWapMergePayRequest alipayRequest = new AlipayTradeWapMergePayRequest();
        // 同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        // 异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        //封装订单数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", paymentInfo.getOrderNo());
        map.put("product_code", "QUICK_WAP_WAY");
        //map.put("total_amount",orderInfo.getTotalAmount());
        map.put("total_amount","0.01");
        map.put("subject",paymentInfo.getContent());
        alipayRequest.setBizContent(JSON.toJSONString(map));

        //3 使用支付宝对象AlipayClient，这个对象里面的方法发起支付
        try {
            return alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单;
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }
}
