package com.spzx.product.service;

import com.spzx.product.domain.Brand;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 2/7/2024 18:13 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */


public interface IBrandService {
    /**
     * 查询分类品牌列表
     *
     * @param brand 分类品牌
     * @return 分类品牌集合
     */
    List<Brand> selectBrandList(Brand brand);

    /**
     * 获取品牌详细信息
     * @param id
     * @return
     */
    Brand getBrandInfo(Long id);

    /**
     * 新增品牌
     * @param brand
     * @return
     */
    int insertBrand(Brand brand);

    /**
     * 修改品牌
     * @param brand
     * @return
     */
    int updateBrand(Brand brand);

    /**
     * 删除品牌
     * @param id
     * @return
     */
    int deleteBrandById(Long id);

    /**
     * 获取全部品牌
     * @return
     */
    List<Brand> getAllBrand();
}
