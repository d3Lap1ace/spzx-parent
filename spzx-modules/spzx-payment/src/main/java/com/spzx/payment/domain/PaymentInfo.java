package com.spzx.payment.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 23/7/2024 15:51 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Data
public class PaymentInfo {
    private Long id;

    private Long userId;

    private String orderNo;

    private Short payType;

    private String tradeNo;

    private BigDecimal amount;

    private String content;

    private String paymentStatus;

    private Timestamp callbackTime;

    private String callbackContent;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String updateBy;

    private String createBy;

    private String remark;

    private Boolean delFlag;

}
