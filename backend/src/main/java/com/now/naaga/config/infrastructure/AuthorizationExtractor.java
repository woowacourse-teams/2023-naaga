package com.now.naaga.config.infrastructure;

public interface AuthorizationExtractor<T> {

    String AUTHORIZATION = "Authorization";
    T extract(String request);
}
