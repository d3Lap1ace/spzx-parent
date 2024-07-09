package com.spzx.product.controller;

import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.spzx.common.core.web.domain.AjaxResult.success;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:20 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Tag(name = "商品分类接口管理")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private ICategoryService categoryService;

    @Operation(summary = "获取分类下拉树列表")
    @GetMapping("/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable Long id){
        return success(categoryService.treeSelect(id));
    }

    /**
     * 使用multipartFile获取上传文件  multipartFile的形参(file)须和前端的name一致
     * <input type = "file" name = "file">
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file) throws Exception {
        try {
            categoryService.importData(file);
            return AjaxResult.success("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("导入失败");
        }
    }

    /**
     * 文件导出
     * @param response
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response) {
        categoryService.exportData(response);
    }
}
