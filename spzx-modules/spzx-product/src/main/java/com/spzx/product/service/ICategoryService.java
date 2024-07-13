package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.domain.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:22 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public interface ICategoryService extends IService<Category> {
    /**
     * 获取分类下拉树列表
     * @param id
     * @return
     */
    public List<Category> treeSelect(Long id);

    /**
     * 获取本层id以及上层id
     * @param Id
     * @return
     */
    List<Long> getCategoryByCategoryId(Long Id);

    /**
     * 文件导入
     * @param file
     */
    void importData(MultipartFile file);

    /**
     * 文件导出
     * @param response
     */
    void exportData(HttpServletResponse response);

    /**
     * 查询所有一级分类
     * @return
     */
    List<CategoryVo> getOneCategory();

    /**
     * h5分类接口
     * @return
     */
    List<CategoryVo> tree();
}
