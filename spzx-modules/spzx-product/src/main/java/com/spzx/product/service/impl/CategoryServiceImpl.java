package com.spzx.product.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;
import com.spzx.product.utils.CategoryUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:23 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 首页获取商品
     * 获取分类下拉树列表
     * @param id
     * @return
     */
    @Override
    public List<Category> treeSelect(Long id) {

        List<Category> list = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId,id));
        Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .forEach(it->{
            long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                    .eq(Category::getParentId,it.getId()));
            if(count>0){
                it.setHasChildren(true);
            }else {
                it.setHasChildren(false);
            }
        });
        return list;
    }

    /**
     * 获取本层id以及上层id
     * @param Id 分类id
     * @return
     */
    @Override
    public List<Long> getCategoryByCategoryId(Long Id) {
        // 创建空集合 用于保存分类id
        ArrayList<Long> list = new ArrayList<>();
        // 调用递归方法  categoryList 上级及本级的id
        List<Category> categoryList = this.getParentCategory(Id,new ArrayList<Category>());

        int size = categoryList.size();
        for(int i = size;i > 0; --i){
            Category category = categoryList.get(i - 1);
            Long id = category.getId();
            list.add(id);
        }
        return list;
    }


    @Override
    public void importData(MultipartFile file) {
        // 使用EasyExcel解析数据
        try {
            // 读取execl里面的数据
            List<CategoryExcelVo> categoryExcelVoList = EasyExcel
                    .read(file.getInputStream())
                    .head(CategoryExcelVo.class)  // 类的类型
                    .sheet()                      // 声明读取excal表
                    .doReadSync();                // doReadSync异步 用于不用监听器的方式  doRead 使用与监听器方式
            //读取到list集合，把list集合添加数据表
            List<Category> categoryList = Optional
                    .of(categoryExcelVoList)
                    .stream()
                    .map(categoryExcelVos -> {
                Category category = new Category();
                BeanUtils.copyProperties(categoryExcelVos, category, Category.class);
                return category;
            }).collect(Collectors.toList());
            this.saveBatch(categoryList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportData(HttpServletResponse response) {


        try {
            // 1. 设置响应结果类型
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");

            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("分类数据", "UTF-8");
            //下载过程
            //如果实现下载，首先基于response对象，设置响应头信息
            // 响应头Content-disposition，文件以下载方式打开
            response.setHeader("Context-disposition","attachment;filename="+fileName+".xlsx");
            response.setHeader("Access-Control-Expose-Headers","content-Disposition");

            //  2. 调用方法查询数据库获取所有分类list集合
            List<Category> categoryList = categoryMapper.selectList(null);

            // 将从数据库中查询到的Category对象转换成CategoryExcelVo对象
            List<CategoryExcelVo> categoryExcelVoList = categoryList.stream()
                    .map(category -> {
                        CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                        BeanUtils.copyProperties(category, categoryExcelVo, CategoryExcelVo.class);
                        return categoryExcelVo;
            }).collect(Collectors.toList());

            // 3. 调用EasyExcel方法实现写操作，把数据库表数据写到Excel表格里面
            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class)
                    .sheet("data")
                    .doWrite(categoryExcelVoList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 远程调用 获取商品一级分类
     * @return
     */
    @Override
    public List<CategoryVo> getOneCategory() {
        // 获得所有的一级分类  parentid == 0 为一级分类
        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, 0));
        // 转为vo视层集合
        List<CategoryVo> categoryVoList = categoryList.stream().map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo, CategoryVo.class);
            return categoryVo;
        }).collect(Collectors.toList());
        return categoryVoList;
    }


    /**
     * h5分类接口
     * @return
     */
    @Override
    public List<CategoryVo> tree() {
        // 将查询的实体分类集合 转为视图分类集合
        List<CategoryVo> categoryVoList  = categoryMapper.selectList(null) //传入null参数，来获取所有类别的列表
                .stream()
                .map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo, CategoryVo.class);
            return categoryVo;
        }).collect(Collectors.toList());
        return CategoryUtils.buildTree(categoryVoList); // 递归方法
    }

    /**
     * 用于递归调用
     * @param Id
     * @param categoryList
     * @return
     */
    private List<Category> getParentCategory(Long Id, ArrayList<Category> categoryList) {

        while (Id > 0){
            Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                    .eq(Category::getId,Id)
                    .select(Category::getId,Category::getParentId));
            categoryList.add(category);
            return getParentCategory(category.getParentId(),categoryList);
        }
        return categoryList;
    }
}
