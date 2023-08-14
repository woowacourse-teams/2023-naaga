package com.now.naaga.auth.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.auth.infrastructure.AuthenticationExtractor;
import com.now.naaga.auth.presentation.dto.MemberRequest;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationExtractor<MemberAuthRequest> authenticationExtractor;

    public MemberAuthArgumentResolver(final AuthenticationExtractor<MemberAuthRequest> authenticationExtractor) {
        this.authenticationExtractor = authenticationExtractor;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && parameter.getParameterType().equals(MemberRequest.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final MemberAuthRequest memberAuthRequest = authenticationExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));
        return new MemberRequest(memberAuthRequest.memberAuth().getMemberId());
    }
}
