package com.now.naaga.common.presentation;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

@Component
public class QueryInspector implements StatementInspector {

    private final QueryCounter queryCounter;

    public QueryInspector(final QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Override
    public String inspect(final String sql) {
        if (isInRequestScope()) {
            queryCounter.increase();
        }
        return sql;
    }

    private boolean isInRequestScope() {
        return Objects.nonNull(RequestContextHolder.getRequestAttributes());
    }
}
