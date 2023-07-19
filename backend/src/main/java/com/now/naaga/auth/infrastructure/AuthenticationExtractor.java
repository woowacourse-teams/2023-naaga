package com.now.naaga.auth.infrastructure;

public interface AuthenticationExtractor<T> {

    String AUTHORIZATION = "Authorization";

    T extract(String request);
}
