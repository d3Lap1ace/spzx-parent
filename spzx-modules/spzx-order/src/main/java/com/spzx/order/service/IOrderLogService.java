package com.spzx.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.domain.OrderLog;

import java.util.List;

/**
 * 订单操作日志记录Service接口
 *
 * @author Mshanshan
 * @date 2024-07-19
 */
public interface IOrderLogService extends IService<OrderLog>
{

    /**
     * 查询订单操作日志记录列表
     *
     * @param orderLog 订单操作日志记录
     * @return 订单操作日志记录集合
     */
    public List<OrderLog> selectOrderLogList(OrderLog orderLog);

}
