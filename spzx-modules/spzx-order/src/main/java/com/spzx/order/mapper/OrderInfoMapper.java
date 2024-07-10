package com.spzx.order.mapper;

import java.util.List;
import com.spzx.order.domain.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 订单Mapper接口
 *
 * @author atguigu
 * @date 2024-07-10
 */

public interface OrderInfoMapper extends BaseMapper<OrderInfo>
{

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);




}
