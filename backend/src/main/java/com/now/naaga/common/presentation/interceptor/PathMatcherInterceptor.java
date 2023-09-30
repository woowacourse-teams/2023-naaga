package com.now.naaga.common.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

public class PathMatcherInterceptor implements HandlerInterceptor {

    private final HandlerInterceptor handlerInterceptor;

    private final PathMatcher pathMatcher;

    private final List<RequestPattern> includedRequestPatterns = new ArrayList<>();

    private final List<RequestPattern> excludedRequestPatterns = new ArrayList<>();


    public PathMatcherInterceptor(final HandlerInterceptor handlerInterceptor,
                                  final PathMatcher pathMatcher) {
        this.handlerInterceptor = handlerInterceptor;
        this.pathMatcher = pathMatcher;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        if (isIncludedRequestPattern(request)) {
            return handlerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }

    public PathMatcherInterceptor includeRequestPattern(final String requestPathPattern,
                                                     final HttpMethod requestPathMethod) {
        this.includedRequestPatterns.add(new RequestPattern(requestPathPattern, requestPathMethod));
        return this;
    }

    public PathMatcherInterceptor excludeRequestPattern(final String requestPathPattern,
                                                     final HttpMethod requestPathMethod) {
        this.excludedRequestPatterns.add(new RequestPattern(requestPathPattern, requestPathMethod));
        return this;
    }


    private boolean isIncludedRequestPattern(final HttpServletRequest request) {
        final String requestPath = request.getServletPath();
        final String requestMethod = request.getMethod();

        final boolean isNotExcludedPattern = excludedRequestPatterns.stream()
                .noneMatch(requestPattern -> requestPattern.match(pathMatcher, requestPath, requestMethod));

        final boolean isIncludedPattern = includedRequestPatterns.stream()
                .anyMatch(requestPattern -> requestPattern.match(pathMatcher, requestPath, requestMethod));

        return isNotExcludedPattern && isIncludedPattern;
    }
}
