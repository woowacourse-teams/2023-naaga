package com.now.naaga.common.config;

import com.now.naaga.auth.presentation.AuthInterceptor;
import com.now.naaga.auth.presentation.AuthenticationArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationArgumentResolver authenticationArgumentResolver;
    private final AuthInterceptor authInterceptor;

    public WebConfig(final AuthenticationArgumentResolver authenticationArgumentResolver,
                     final AuthInterceptor authInterceptor) {
        this.authenticationArgumentResolver = authenticationArgumentResolver;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationArgumentResolver);
    }
}
