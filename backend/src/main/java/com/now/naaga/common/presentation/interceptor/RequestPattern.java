package com.now.naaga.common.presentation.interceptor;

import org.springframework.http.HttpMethod;
import org.springframework.util.PathMatcher;

public record RequestPattern(String path,
                             HttpMethod method) {

    public boolean match(final PathMatcher pathMatcher,
                         final String requestPath,
                         final String requestMethod) {
        final HttpMethod method = HttpMethod.valueOf(requestMethod.toUpperCase());

        return pathMatcher.match(path, requestPath)
                && this.method == method;
    }
}
