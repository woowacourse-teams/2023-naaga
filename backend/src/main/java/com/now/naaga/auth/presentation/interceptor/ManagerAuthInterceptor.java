package com.now.naaga.auth.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.Objects;

@Component
public class ManagerAuthInterceptor implements HandlerInterceptor {

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String decodedHeader = new String(Base64.getDecoder().decode(header));
        final String[] idAndPassword = decodedHeader.split(":");
        final String inputId = idAndPassword[0].trim();
        final String inputPassword = idAndPassword[1].trim();
        if (Objects.equals(id, inputId) && Objects.equals(password, inputPassword)) {
            return true;
        }
        throw new IllegalArgumentException("허용되지 않은 관리자입니다");
    }
}
