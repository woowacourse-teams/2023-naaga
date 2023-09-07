package com.now.naaga.common;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;

public class LogFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final QueryCounter queryCounter;

    public LogFilter(final QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        queryCounter.init();
        final long start = System.currentTimeMillis();

        chain.doFilter(request, response);

        log(response,start);
        queryCounter.close();
    }

    private void log(final ServletResponse response,
                     final long start) {
        final int queryCount = queryCounter.count();
        final long end = System.currentTimeMillis();
        final long time = end - start;
        MDC.put("time", String.valueOf(time)+"ms");
        MDC.put("queryCount", String.valueOf(queryCount));
        log.info("queryCount: {}", queryCount);
    }

}
