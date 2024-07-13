package com.spzx.channel.controller;

import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 18:44 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    @Autowired
    private IBrandService brandService;

    @Operation(summary = "获取全部品牌")
    @GetMapping("getBrandAll")
    public AjaxResult selectBrandAll() {
        return success(brandService.getBrandAll());
    }

}
