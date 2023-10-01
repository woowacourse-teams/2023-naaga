package com.now.naaga.auth.presentation.interceptor;

import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.auth.exception.AuthExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.Objects;

import static com.now.naaga.auth.exception.AuthExceptionType.*;

@Component
public class ManagerAuthInterceptor implements HandlerInterceptor {

    public static final int AUTH_HEADER_INFO_SIZE = 2;

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        final String[] idAndPassword = extractHeaderInfo(request);
        return loginForManager(idAndPassword);
    }

    private String[] extractHeaderInfo(final HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new AuthException(NOT_EXIST_HEADER);
        }
        final String decodedHeader = new String(Base64.getDecoder().decode(header));
        final String[] idAndPassword = decodedHeader.split(":");
        if (idAndPassword.length != AUTH_HEADER_INFO_SIZE) {
            throw new AuthException(INVALID_HEADER);
        }
        return idAndPassword;
    }

    private boolean loginForManager(final String[] idAndPassword) {
        final String inputId = idAndPassword[0].trim();
        final String inputPassword = idAndPassword[1].trim();
        if (Objects.equals(id, inputId) && Objects.equals(password, inputPassword)) {
            return true;
        }
        throw new AuthException(INVALID_MANAGER);
    }
}
