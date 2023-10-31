package com.now.naaga.common.config;

import static org.hibernate.cfg.AvailableSettings.STATEMENT_INSPECTOR;

import com.now.naaga.common.presentation.QueryInspector;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    private final QueryInspector queryInspector;

    public HibernateConfig(final QueryInspector queryInspector) {
        this.queryInspector = queryInspector;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> hibernateProperties.put(STATEMENT_INSPECTOR, queryInspector);
    }
}
