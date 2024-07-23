package com.spzx.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.order.api.RemoteOrderInfoService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.mapper.PaymentInfoMapper;
import com.spzx.payment.service.IPaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 23/7/2024 16:30 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Service
public class PaymentInfoImpl implements IPaymentInfoService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;


    /**
     * 保存支付信息
     * @param orderNo
     * @return
     */
    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        // 根据订单号查询支付实体
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        if (paymentInfo == null) {
            R<OrderInfo> orderInfoR = remoteOrderInfoService.getByOrderNo(orderNo, SecurityConstants.INNER);
            if(R.FAIL == orderInfoR.getCode()) {
                throw new ServiceException(orderInfoR.getMsg());
            }
            OrderInfo orderInfo = orderInfoR.getData();
            paymentInfo = new PaymentInfo();
            paymentInfo.setOrderNo(orderNo);
            paymentInfo.setUserId(orderInfo.getUserId());
            String content = "";
            for(OrderItem item : orderInfo.getOrderItemList()) {
                content += item.getSkuName() + " ";
            }
            paymentInfo.setContent(content);
            paymentInfo.setAmount(orderInfo.getTotalAmount());
            paymentInfo.setPaymentStatus("0");
            paymentInfoMapper.insert(paymentInfo);
        }

        return paymentInfo;
    }

    @Override
    public void updatePaymentStatus(Map<String, String> paramMap, String i) {
        String orderNo = paramMap.get("orderNo");
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        paymentInfo.setPaymentStatus(i);
    }
}
