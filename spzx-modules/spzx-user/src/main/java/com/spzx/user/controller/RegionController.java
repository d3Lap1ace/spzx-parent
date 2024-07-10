package com.spzx.user.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.user.service.IRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 10/7/2024 16:11 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@RestController
@RequestMapping("/region")
public class RegionController extends BaseController {

    @Autowired
    private IRegionService regionService;

    @GetMapping(value = "/treeSelect/{parentCode}")
    public AjaxResult treeSelect(@PathVariable String parentCode){
        return success(regionService.treeSelect(parentCode));
    }
}
