package com.spzx.payment.service;

import com.spzx.payment.domain.PaymentInfo;

import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 23/7/2024 14:45 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IPaymentInfoService {
    PaymentInfo savePaymentInfo(String orderNo);

    void updatePaymentStatus(Map<String, String> paramMap, String i);
}
