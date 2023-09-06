package com.now.naaga.auth.presentation.interceptor;

import com.now.naaga.auth.infrastructure.AuthenticationExtractor;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthenticationExtractor<MemberAuth> authenticationExtractor;

    public AuthInterceptor(final AuthenticationExtractor<MemberAuth> authenticationExtractor) {
        this.authenticationExtractor = authenticationExtractor;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        final MemberAuth memberAuth = authenticationExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));
        return memberAuth.getMemberId() != null;
    }
}
