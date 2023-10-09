package com.now.naaga.common.presentation;

public enum MdcToken {

    REQUEST_ID("request_id")
    ;

    private final String key;

    MdcToken(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
