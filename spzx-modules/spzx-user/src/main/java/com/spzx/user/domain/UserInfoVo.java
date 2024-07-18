package com.spzx.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 18/7/2024 13:37 周四
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */


@Data
@Schema(description = "用户类")
public class UserInfoVo {

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像")
    private String avatar;

}
