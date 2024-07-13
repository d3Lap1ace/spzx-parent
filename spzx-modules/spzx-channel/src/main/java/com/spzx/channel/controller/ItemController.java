package com.spzx.channel.controller;

import com.spzx.channel.service.IItemService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 21:07 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Tag(name = "商品详情接口")
@RestController
@RequestMapping("/item")
public class ItemController extends BaseController {

    @Autowired
    private IItemService itemService;


    @Operation(summary = "商品详情")
    @GetMapping("/{skuId}")
    public AjaxResult getItem(@PathVariable Long skuId) {
        return success(itemService.item(skuId));
    }


}
