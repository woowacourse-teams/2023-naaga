package com.now.naaga.common;

import org.springframework.stereotype.Component;

@Component
public class QueryCounter {

    private static final int INITIAL_VALUE = 0;
    private static final int WARNING_COUNT = 10;

    private final ThreadLocal<Integer> count = new ThreadLocal<>();

    public void increase() {
        if (count.get() == null) {
            init();
        }
        count.set(count.get() + 1);
    }

    public void init() {
        count.set(INITIAL_VALUE);
    }

    public int count() {
        return count.get();
    }

    public void close() {
        count.remove();
    }
}
