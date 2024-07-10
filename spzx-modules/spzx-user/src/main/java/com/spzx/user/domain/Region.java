package com.spzx.user.domain;

import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 10/7/2024 16:45 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Data
@Schema(description = "地区信息")
public class Region extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Excel(name = "地区编码")
    @Schema(description = "地区编码")
    private String code;


    @Excel(name = "上级地区")
    @Schema(description = "上级地区")
    private String parentCode;

    @Excel(name = "地区名称")
    @Schema(description = "地区名称")
    private String name;

    @Excel(name = "地区级别")
    @Schema(description = "地区级别")
    private Integer level;
}
