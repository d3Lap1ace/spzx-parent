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
@Schema(description = "商品详情")
public class ProductDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @Schema(description = "商品id")
    private Long productId;

    /**
     * 详情图片地址
     */
    @Schema(description = "详情图片地址")
    private String imageUrls;
}
