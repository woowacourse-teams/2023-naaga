package com.now.naaga.common.config;

import com.now.naaga.common.LogFilter;
import com.now.naaga.common.MdcFilter;
import com.now.naaga.common.QueryCounter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private static final int FIRST = 0;
    private static final int SECOND = 1;

    private final QueryCounter queryCounter;

    public FilterConfig(final QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Bean
    FilterRegistrationBean<MdcFilter> mdcFilter() {
        MdcFilter mdcFilter = new MdcFilter();
        FilterRegistrationBean<MdcFilter> registrationBean = new FilterRegistrationBean<>(mdcFilter);
        registrationBean.setOrder(FIRST);
        return registrationBean;
    }

    @Bean
    FilterRegistrationBean<LogFilter> logFilter() {
        LogFilter logFilter = new LogFilter(queryCounter);
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>(logFilter);
        registrationBean.setOrder(SECOND);
        return registrationBean;
    }
}
