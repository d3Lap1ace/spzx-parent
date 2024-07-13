package com.spzx.product.api.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo
{

    /** 分类id */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 图标地址 */
    private String imageUrl;

    /** 上级分类id */
    private Long parentId;

    /** 下级分类 */
    private List<CategoryVo> children;

}