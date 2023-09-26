package com.now.naaga.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static com.now.naaga.common.MdcToken.*;

public class LogFilter implements Filter {

    private final static String LOG_FORMAT = "uri: {}, method: {}, time: {}ms, queryCount: {}";

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
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        MDC.put(REQUEST_ID.getKey(), UUID.randomUUID().toString());

        chain.doFilter(request, response);

        final int queryCount = queryCounter.count();
        log(start, queryCount, httpServletRequest);
        queryCounter.close();
        MDC.clear();
    }

    private void log(final long start,
                     final int queryCount,
                     final HttpServletRequest httpServletRequest) {
        final long end = System.currentTimeMillis();
        final long time = end - start;
        log.info(LOG_FORMAT, httpServletRequest.getRequestURI(), httpServletRequest.getMethod(), time, queryCount);
    }
}
