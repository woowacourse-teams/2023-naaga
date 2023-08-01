package com.now.naaga.auth.presentation;

import com.now.naaga.auth.application.AuthService;
import com.now.naaga.auth.infrastructure.AuthenticationExtractor;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthenticationExtractor<MemberAuthRequest> authenticationExtractor;
    private final AuthService authService;

    public AuthInterceptor(final AuthenticationExtractor<MemberAuthRequest> authenticationExtractor,
                           final AuthService authService) {
        this.authenticationExtractor = authenticationExtractor;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        final MemberAuthRequest memberAuthRequest = authenticationExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));
        authService.validateAuthentication(memberAuthRequest);
        return true;
    }
}
