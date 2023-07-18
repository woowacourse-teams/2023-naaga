package com.now.naaga.config;

import com.now.naaga.config.infrastructure.AuthorizationExtractor;
import com.now.naaga.member.dto.MemberRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.now.naaga.config.infrastructure.AuthorizationExtractor.AUTHORIZATION;

@Component
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    final AuthorizationExtractor<MemberRequest> authorizationExtractor;

    public AuthenticationArgumentResolver(AuthorizationExtractor<MemberRequest> authorizationExtractor) {
        this.authorizationExtractor = authorizationExtractor;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                || parameter.getParameterType().isInstance(MemberRequest.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) throws Exception {
        final String header = webRequest.getHeader(AUTHORIZATION);
        return authorizationExtractor.extract(header);
    }
}
