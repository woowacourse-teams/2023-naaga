package com.now.naaga.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static com.now.naaga.common.MdcToken.*;

public class MdcFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        MDC.put(REQUEST_ID.getKey(), UUID.randomUUID().toString());
        MDC.put(URI.getKey(), httpServletRequest.getRequestURI());
        MDC.put(METHOD.getKey(), httpServletRequest.getMethod());

        chain.doFilter(request, response);
    }

}
