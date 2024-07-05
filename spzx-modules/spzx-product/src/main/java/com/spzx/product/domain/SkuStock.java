package com.spzx.product.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 4/7/2024 11:44 周四
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Data
@Schema(description = "商品sku库存")
public class SkuStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "skuID")
    private Long skuId;

    @Schema(description = "总库存数")
    private Integer totalNum;

    @Schema(description = "锁定库存数")
    private Integer lockNum;

    @Schema(description = "可用库存数")
    private Integer availableNum;

    @Schema(description = "销量")
    private Integer saleNum;

    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Integer status;
}
