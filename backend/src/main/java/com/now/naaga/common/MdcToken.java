package com.now.naaga.common;

public enum MdcToken {
    REQUEST_ID("request_id"),
    URI("uri"),
    TIME("time"),
    QUERYCOUNT("queryCount"),
    METHOD("method");

    private final String key;

    MdcToken(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
