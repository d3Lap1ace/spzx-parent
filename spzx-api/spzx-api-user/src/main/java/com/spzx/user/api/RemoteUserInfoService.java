package com.spzx.user.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.api.factory.RemoteUserInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 17/7/2024 14:29 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@FeignClient(contextId = "remoteUserInfoService",value = ServiceNameConstants.USER_SERVICE,fallbackFactory = RemoteUserInfoFallbackFactory.class)
public interface RemoteUserInfoService {
    @PostMapping("/userInfo/register")
    R<Boolean> register(@RequestBody UserInfo userInfo, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/userInfo/info/{username}")
    R<UserInfo> getUserInfo(@PathVariable("username") String username,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @PutMapping("/userInfo/updateUserLogin")
    R<Boolean> updateUserLogin(@RequestBody UpdateUserLogin updateUserLogin,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
