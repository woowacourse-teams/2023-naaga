package com.now.naaga.auth.presentation.interceptor;

import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_HEADER;
import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_MANAGER;
import static com.now.naaga.auth.exception.AuthExceptionType.NOT_EXIST_HEADER;

import com.now.naaga.auth.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ManagerAuthInterceptor implements HandlerInterceptor {

    public static final int AUTH_HEADER_INFO_SIZE = 2;

    public static final String AUTH_HEADER_TYPE = "Basic";

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        final List<String> idAndPassword = extractHeaderInfo(request);
        return loginForManager(idAndPassword);
    }

    private List<String> extractHeaderInfo(final HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new AuthException(NOT_EXIST_HEADER);
        }
        final String[] authHeader = header.split(" ");
        if (!AUTH_HEADER_TYPE.equalsIgnoreCase(authHeader[0])) {
            throw new AuthException(INVALID_HEADER);
        }
        final String decodedHeader = new String(Base64.getDecoder().decode(authHeader[1]));
        final String[] idAndPassword = decodedHeader.split(":");
        if (idAndPassword.length != AUTH_HEADER_INFO_SIZE) {
            throw new AuthException(INVALID_HEADER);
        }
        return Arrays.asList(idAndPassword);
    }

    private boolean loginForManager(final List<String> idAndPassword) {
        final String inputId = idAndPassword.get(0).strip();
        final String inputPassword = idAndPassword.get(1).strip();
        if (Objects.equals(id, inputId) && Objects.equals(password, inputPassword)) {
            return true;
        }
        throw new AuthException(INVALID_MANAGER);
    }
}
