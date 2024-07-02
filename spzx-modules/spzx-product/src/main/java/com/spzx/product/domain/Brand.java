package com.spzx.product.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 2/7/2024 18:03 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Data
@Schema(description = "品牌")
public class Brand extends BaseEntity {

    private static final long serialVersionUID = 1L;


    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌图标")
    private String logo;

}
