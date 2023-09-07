package com.now.naaga.common;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hibernate.cfg.AvailableSettings.STATEMENT_INSPECTOR;

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
