package com.now.naaga.common.presentation.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;


public class RequestMatcher {

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    private RequestMatcher() {}

    public static boolean match(final RequestPattern requestPattern,
                                final RequestPattern inputRequestPattern) {
        return pathMatcher.match(requestPattern.path(), inputRequestPattern.path())
                && requestPattern.method() == inputRequestPattern.method();
    }
}
