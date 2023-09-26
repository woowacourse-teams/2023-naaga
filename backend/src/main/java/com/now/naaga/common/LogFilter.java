package com.now.naaga.common;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;

import static com.now.naaga.common.MdcToken.QUERY_COUNT;
import static com.now.naaga.common.MdcToken.TIME;

public class LogFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final QueryCounter queryCounter;

    public LogFilter(final QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {
        queryCounter.init();
        final long start = System.currentTimeMillis();

        chain.doFilter(request, response);

        log(response, start);
        queryCounter.close();
        MDC.clear();
    }

    private void log(final ServletResponse response,
                     final long start) {
        final int queryCount = queryCounter.count();
        final long end = System.currentTimeMillis();
        final long time = end - start;
        MDC.put(TIME.getKey(), String.valueOf(time) + "ms");
        MDC.put(QUERY_COUNT.getKey(), String.valueOf(queryCount));
        log.info("level: {}", "info");
    }
}
