package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:56 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Data
public class CategoryBrand extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "品牌ID")
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "分类名称")
    @TableField(exist = false)
    private String categoryName;

    @Schema(description = "品牌名称")
    @TableField(exist = false)
    private String brandName;

    @Schema(description = "品牌图标")
    @TableField(exist = false)
    private String logo;

    @Schema(description = "分类id列表")
    @TableField(exist = false)
    private List<Long> categoryIdList;
}
