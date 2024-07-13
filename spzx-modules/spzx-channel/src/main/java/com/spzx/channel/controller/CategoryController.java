package com.spzx.channel.controller;

import com.spzx.channel.service.ICategoryService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 18:19 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/tree")
    public AjaxResult tree() {
        return success(categoryService.tree());
    }




}
