package com.spzx.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.TradeVo;

import java.util.List;

/**
 * 订单Service接口
 *
 * @author atguigu
 * @date 2024-07-10
 */
public interface IOrderInfoService extends IService<OrderInfo>
{

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    TradeVo orderTradeData();

    Long submitOrder(OrderForm orderForm);

    TradeVo buy(Long skuId);

    List<OrderInfo> selectUserOrderInfoList(Integer orderStatus);

    void processCloseOrder(long orderId);

    void cancelOrder(Long orderId);

    OrderInfo getByOrderNo(String orderNo);

    OrderInfo getOrderInfo(Long orderId);
}
