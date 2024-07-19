package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.order.domain.OrderLog;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.order.service.IOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单操作日志记录Service业务层处理
 *
 * @author Mshanshan
 * @date 2024-07-19
 */
@Service
public class OrderLogServiceImpl extends ServiceImpl<OrderLogMapper, OrderLog> implements IOrderLogService
{
    @Autowired
    private OrderLogMapper orderLogMapper;

    /**
     * 查询订单操作日志记录列表
     *
     * @param orderLog 订单操作日志记录
     * @return 订单操作日志记录
     */
    @Override
    public List<OrderLog> selectOrderLogList(OrderLog orderLog)
    {
        return orderLogMapper.selectOrderLogList(orderLog);
    }

}
