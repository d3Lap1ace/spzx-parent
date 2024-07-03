package com.spzx.product.mapper;

import com.spzx.product.domain.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 2/7/2024 18:15 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */


public interface BrandMapper {

    List<Brand> selectBrandList(Brand brand);


    @Select("select * from brand where id = #{id}")
    Brand getBrandInfo(Long id);


    int insertBrand(Brand brand);

    int updateBrand(Brand brand);


    int deleteBrandById(Long[] ids);


}
