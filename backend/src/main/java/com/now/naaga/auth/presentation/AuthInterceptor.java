package com.now.naaga.auth.presentation;

import com.now.naaga.auth.application.AuthService;
import com.now.naaga.auth.infrastructure.AuthenticationExtractor;
import com.now.naaga.member.application.dto.MemberCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTH_KEY = "memberCommand";

    private final AuthenticationExtractor<MemberCommand> authenticationExtractor;
    private final AuthService authService;

    public AuthInterceptor(final AuthenticationExtractor<MemberCommand> authenticationExtractor, final AuthService authService) {
        this.authenticationExtractor = authenticationExtractor;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final MemberCommand memberCommand = authenticationExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));
        authService.validateAuthentication(memberCommand);
        request.setAttribute(AUTH_KEY, memberCommand);
        return true;
    }
}
