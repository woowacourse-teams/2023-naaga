package com.now.naaga.common.config;

import com.now.naaga.auth.presentation.AuthInterceptor;
import com.now.naaga.auth.presentation.PlayerArgumentResolver;

import java.util.List;

import com.now.naaga.auth.presentation.MemberAuthArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PlayerArgumentResolver playerArgumentResolver;
    private final MemberAuthArgumentResolver memberAuthArgumentResolver;
    private final AuthInterceptor authInterceptor;

    public WebConfig(final PlayerArgumentResolver playerArgumentResolver,
                     final MemberAuthArgumentResolver memberAuthArgumentResolver,
                     final AuthInterceptor authInterceptor) {
        this.playerArgumentResolver = playerArgumentResolver;
        this.memberAuthArgumentResolver = memberAuthArgumentResolver;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/h2-console/**")
                .excludePathPatterns("/auth/**")
                .excludePathPatterns("/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.ico")
                .excludePathPatterns("/ranks");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(playerArgumentResolver);
        resolvers.add(memberAuthArgumentResolver);
    }
}
