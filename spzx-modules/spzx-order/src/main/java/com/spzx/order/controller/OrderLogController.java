package com.spzx.order.controller;

import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.order.domain.OrderLog;
import com.spzx.order.service.IOrderLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 订单操作日志记录Controller
 *
 * @author Mshanshan
 * @date 2024-07-19
 */
@Tag(name = "订单操作日志记录接口管理")
@RestController
@RequestMapping("/orderLog")
public class OrderLogController extends BaseController
{
    @Autowired
    private IOrderLogService orderLogService;

    /**
     * 查询订单操作日志记录列表
     */
    @Operation(summary = "查询订单操作日志记录列表")
    @RequiresPermissions("order:orderLog:list")
    @GetMapping("/list")
    public TableDataInfo list(OrderLog orderLog)
    {
        startPage();
        List<OrderLog> list = orderLogService.selectOrderLogList(orderLog);
        return getDataTable(list);
    }

    /**
     * 导出订单操作日志记录列表
     */
    @Operation(summary = "导出订单操作日志记录列表")
    @RequiresPermissions("order:orderLog:export")
    @Log(title = "订单操作日志记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderLog orderLog)
    {
        List<OrderLog> list = orderLogService.selectOrderLogList(orderLog);
        ExcelUtil<OrderLog> util = new ExcelUtil<OrderLog>(OrderLog.class);
        util.exportExcel(response, list, "订单操作日志记录数据");
    }

    /**
     * 获取订单操作日志记录详细信息
     */
    @Operation(summary = "获取订单操作日志记录详细信息")
    @RequiresPermissions("order:orderLog:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderLogService.getById(id));
    }

    /**
     * 新增订单操作日志记录
     */
    @Operation(summary = "新增订单操作日志记录")
    @RequiresPermissions("order:orderLog:add")
    @Log(title = "订单操作日志记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderLog orderLog)
    {
        return toAjax(orderLogService.save(orderLog));
    }

    /**
     * 修改订单操作日志记录
     */
    @Operation(summary = "修改订单操作日志记录")
    @RequiresPermissions("order:orderLog:edit")
    @Log(title = "订单操作日志记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderLog orderLog)
    {
        return toAjax(orderLogService.updateById(orderLog));
    }

    /**
     * 删除订单操作日志记录
     */
    @Operation(summary = "删除订单操作日志记录")
    @RequiresPermissions("order:orderLog:remove")
    @Log(title = "订单操作日志记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(orderLogService.removeBatchByIds(Arrays.asList(ids)));
    }
}
