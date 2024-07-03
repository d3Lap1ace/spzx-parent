package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 14:13 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Data
@Schema(name = "商品分类")
public class Category extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 分类名称 */
    @Schema(description = "分类名称")
    private String name;

    /** 图标地址 */
    @Schema(description = "图标地址")
    private String imageUrl;

    /** 上级分类id */
    @Schema(description = "上级分类id")
    private Long parentId;

    /** 是否显示[0-不显示，1显示] */
    @Schema(description = "是否显示[0-不显示，1显示]")
    private Integer status;

    /** 排序 */
    @Schema(description = "排序")
    private Long orderNum;

    /** 是否有子节点 */
    @TableField(exist = false)
    private Boolean hasChildren;

    /** 子节点列表 */
    @TableField(exist = false)
    private List<Category> children;

}
