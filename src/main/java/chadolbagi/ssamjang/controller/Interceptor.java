package chadolbagi.ssamjang.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class Interceptor extends HandlerInterceptorAdapter {

    @Value("${spring.auth}")
    String auth;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String remoteAddr = getRemoteAddr(request);
        if (isAdmin(remoteAddr) == false && isAuth(request) == false) {
            response.setStatus(404);
            return false;
        }
        return true;
    }

    private boolean isAdmin(String remoteAddr) {
        return remoteAddr.equals("127.0.0.1") || remoteAddr.startsWith("0:0:0:0") || remoteAddr.startsWith("172.31.") || remoteAddr.startsWith("10.10.") || remoteAddr.startsWith("192.168.");
    }

    private boolean isAuth(HttpServletRequest request) {
        return auth.equals(request.getParameter("auth"));
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }
}
