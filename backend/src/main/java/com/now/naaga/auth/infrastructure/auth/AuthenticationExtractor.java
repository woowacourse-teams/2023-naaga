package com.now.naaga.auth.infrastructure.auth;

public interface AuthenticationExtractor<T> {

    T extract(final String request);
}
