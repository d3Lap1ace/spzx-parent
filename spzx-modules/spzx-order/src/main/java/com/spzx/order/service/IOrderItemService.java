package com.spzx.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.api.domain.OrderItem;

import java.util.List;

/**
 * 订单项信息Service接口
 *
 * @author atguigu
 * @date 2024-07-10
 */
public interface IOrderItemService extends IService<OrderItem>
{

    /**
     * 查询订单项信息列表
     *
     * @param orderItem 订单项信息
     * @return 订单项信息集合
     */
    public List<OrderItem> selectOrderItemList(OrderItem orderItem);

}
