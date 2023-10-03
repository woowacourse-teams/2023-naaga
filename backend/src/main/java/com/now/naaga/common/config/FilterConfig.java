package com.now.naaga.common.config;

import com.now.naaga.common.presentation.LogFilter;
import com.now.naaga.common.presentation.QueryCounter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final QueryCounter queryCounter;

    public FilterConfig(final QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        final LogFilter logFilter = new LogFilter(queryCounter);
        return new FilterRegistrationBean<>(logFilter);
    }
}
