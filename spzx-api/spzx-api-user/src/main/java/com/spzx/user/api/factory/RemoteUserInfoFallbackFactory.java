package com.spzx.user.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.api.RemoteUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 17/7/2024 14:32 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Component
public class RemoteUserInfoFallbackFactory implements FallbackFactory<RemoteUserInfoService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteUserInfoFallbackFactory.class);

    @Override
    public RemoteUserInfoService create(Throwable cause) {
        log.error("用户服务调用失败:{}", cause.getMessage());
        return new RemoteUserInfoService() {
            @Override
            public R<Boolean> register(UserInfo userInfo, String source) {
                return R.fail("会员注册失败:" + cause.getMessage());
            }

            @Override
            public R<UserInfo> getUserInfo(String username, String source) {
                return R.fail("获取会员信息失败"+cause.getMessage());
            }

            @Override
            public R<Boolean> updateUserLogin(UpdateUserLogin updateUserLogin, String source) {
                return R.fail("跟新会员登录信息失败"+cause.getMessage());
            }
        };
    }
}