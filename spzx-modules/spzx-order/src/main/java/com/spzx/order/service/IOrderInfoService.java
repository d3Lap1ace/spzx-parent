package com.spzx.order.service;

import java.util.List;
import com.spzx.order.domain.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
