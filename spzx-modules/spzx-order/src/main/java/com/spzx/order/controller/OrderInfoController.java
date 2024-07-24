package com.spzx.order.controller;

import com.github.pagehelper.PageHelper;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.service.IOrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 订单Controller
 *
 * @author atguigu
 * @date 2024-07-10
 */
@Tag(name = "订单接口管理")
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController extends BaseController
{
    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 查询订单列表
     */
    @Operation(summary = "查询订单列表")
    @RequiresPermissions("order:orderInfo:list")
    @GetMapping("/list")
    public TableDataInfo list(OrderInfo orderInfo)
    {
        startPage();
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @Operation(summary = "导出订单列表")
    @RequiresPermissions("order:orderInfo:export")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderInfo orderInfo)
    {
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        ExcelUtil<OrderInfo> util = new ExcelUtil<OrderInfo>(OrderInfo.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @Operation(summary = "获取订单详细信息")
    @RequiresPermissions("order:orderInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderInfoService.getById(id));
    }

    /**
     * 新增订单
     */
    @Operation(summary = "新增订单")
    @RequiresPermissions("order:orderInfo:add")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.save(orderInfo));
    }

    /**
     * 修改订单
     */
    @Operation(summary = "修改订单")
    @RequiresPermissions("order:orderInfo:edit")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderInfo orderInfo)
    {
        return toAjax(orderInfoService.updateById(orderInfo));
    }

    /**
     * 删除订单
     */
    @Operation(summary = "删除订单")
    @RequiresPermissions("order:orderInfo:remove")
    @Log(title = "订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(orderInfoService.removeBatchByIds(Arrays.asList(ids)));
    }

    @Operation(summary = "订单结算")
    @RequiresLogin
    @GetMapping("/trade")
    public AjaxResult orderTradeData() {
        return success(orderInfoService.orderTradeData());
    }

    @Operation(summary = "用户提交订单")
    @RequiresLogin
    @PostMapping("/submitOrder")
    public AjaxResult submitOrder(@RequestBody OrderForm orderForm) {
        return success(orderInfoService.submitOrder(orderForm));
    }

    @Operation(summary = "立即购买")
    @RequiresLogin
    @GetMapping("buy/{skuId}")
    public AjaxResult buy(@PathVariable Long skuId) {
        return success(orderInfoService.buy(skuId));
    }

    @Operation(summary = "获取订单信息")
    @RequiresLogin
    @GetMapping("getOrderInfo/{orderId}")
    public AjaxResult getOrderInfo(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return success(orderInfo);
    }

    @Operation(summary = "获取用户订单分页列表")
    @GetMapping("/userOrderInfoList/{pageNum}/{pageSize}")
    public TableDataInfo list(@PathVariable Integer pageNum, @PathVariable Integer pageSize,Integer orderStatus){
        PageHelper.startPage(pageNum,pageSize);
        List<OrderInfo> userOrderInfoList = orderInfoService.selectUserOrderInfoList(orderStatus);
        return getDataTable(userOrderInfoList);
    }

    @Operation(summary = "取消订单")
    @RequiresLogin
    @GetMapping("cancelOrder/{orderId}")
    public AjaxResult cancelOrder(@PathVariable Long orderId) {
        orderInfoService.cancelOrder(orderId);
        return success();
    }

    @Operation(summary = "根据订单号获取订单信息")
    @InnerAuth
    @GetMapping("getByOrderNo/{orderNo}")
    public R<OrderInfo> getByOrderNo(@PathVariable String orderNo) {
        OrderInfo orderInfo = orderInfoService.getById(orderNo);
        return R.ok(orderInfo);
    }



}
