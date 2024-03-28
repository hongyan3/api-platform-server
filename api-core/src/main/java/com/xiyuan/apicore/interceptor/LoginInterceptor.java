package com.xiyuan.apicore.interceptor;

import com.xiyuan.apicommon.model.enums.ErrorCode;
import com.xiyuan.apicore.constant.UserConstant;
import com.xiyuan.apicommon.exception.BusinessException;
import com.xiyuan.apicommon.model.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")){
            return true;
        }
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return true;
    }
}
