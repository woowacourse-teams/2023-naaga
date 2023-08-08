package com.now.naaga.common.config;

import com.now.naaga.auth.presentation.AuthInterceptor;
import com.now.naaga.auth.presentation.AuthArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final AuthInterceptor authInterceptor;

    public WebConfig(final AuthArgumentResolver authArgumentResolver,
                     final AuthInterceptor authInterceptor) {
        this.authArgumentResolver = authArgumentResolver;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        //registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/h2-console/**")
//                .excludePathPatterns("/ranks")
//                .excludePathPatterns("/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif")
        ;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
