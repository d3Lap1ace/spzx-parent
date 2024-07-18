package com.spzx.auth.controller;

import com.spzx.auth.form.LoginBody;
import com.spzx.auth.form.RegisterBody;
import com.spzx.auth.service.H5LoginService;
import com.spzx.auth.service.SysLoginService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.JwtUtils;
import com.spzx.common.security.auth.AuthUtil;
import com.spzx.common.security.service.TokenService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 17/7/2024 15:14 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@RestController
public class H5TokenController {

    @Autowired
    private H5LoginService h5LoginService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("/h5/register")
    public R<?> register(@RequestBody RegisterBody registerBody){

        h5LoginService.register(registerBody);
        return R.ok();
    }
    @PostMapping("/h5/login")
    public R<?> login(@RequestBody LoginBody loginBody){
        LoginUser userInfo = h5LoginService.login(loginBody.getUsername(),loginBody.getPassword());
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("/h5/logout")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

}
