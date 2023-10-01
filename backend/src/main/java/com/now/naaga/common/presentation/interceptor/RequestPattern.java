package com.now.naaga.common.presentation.interceptor;

import org.springframework.http.HttpMethod;

public record RequestPattern(String path,
                             HttpMethod method) {

    public boolean match(final String inputPath,
                         final String inputMethodAsString) {
        final HttpMethod inputMethod = HttpMethod.valueOf(inputMethodAsString.toUpperCase());
        final RequestPattern inputRequestPattern = new RequestPattern(inputPath, inputMethod);

        return RequestMatcher.match(this, inputRequestPattern);
    }
}
