package com.ihankun.core.base.api;

import com.ihankun.core.base.context.LoginUserContext;
import com.ihankun.core.base.context.LoginUserInfo;

/**
 * @author hankun
 */
public interface BaseService {

    default Long getOrgId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null && loginUser.getOrgId() != null ? loginUser.getOrgId() : BasePO.DEFAULT_ID;
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    default LoginUserInfo getLoginUser() {
        return LoginUserContext.getLoginUserInfo();
    }
}
