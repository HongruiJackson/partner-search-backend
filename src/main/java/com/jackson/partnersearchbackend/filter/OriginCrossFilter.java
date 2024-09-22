package com.jackson.partnersearchbackend.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class OriginCrossFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String allowOrigin = request.getHeader("Origin");
        String allowHeader = request.getHeader("Access-Control-Request-Headers");
//        System.out.println(allowOrigin);
//        System.out.println(allowHeader);
        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        //设置允许带cookie的请求
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", allowHeader);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
