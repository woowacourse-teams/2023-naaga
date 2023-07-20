package com.now.naaga.auth.infrastructure;

public interface AuthenticationExtractor<T> {

    T extract(final String request);
}
