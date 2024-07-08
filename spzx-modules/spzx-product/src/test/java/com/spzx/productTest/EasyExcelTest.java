package com.spzx.productTest;

import com.alibaba.excel.EasyExcel;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.service.ICategoryService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 8/7/2024 19:04 周一
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@SpringBootTest
public class EasyExcelTest {
    @Autowired
    ICategoryService categoryService;


    @Test
    public void wirteDataToExcel(){
        List<CategoryExcelVo> list = categoryService
                .list()
                .stream()
                .map(category -> {
                    CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                    BeanUtils.copyProperties(category, categoryExcelVo);
                    return categoryExcelVo;
                }).collect(Collectors.toList());
        EasyExcel.write("F:\\export.xlsx", CategoryExcelVo.class).sheet("分类数据").doWrite(list);

    }
}
