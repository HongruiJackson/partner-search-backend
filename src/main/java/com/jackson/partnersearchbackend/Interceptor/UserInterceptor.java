package com.jackson.partnersearchbackend.Interceptor;

import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.jackson.partnersearchbackend.constant.UserConstant.USER_LOGIN_STATE;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        return true;

    }
}
