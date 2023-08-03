package com.now.naaga.common.exception;

public class ExceptionResponse {

    private int code;
    private String message;

    public ExceptionResponse() {
    }

    public ExceptionResponse(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ExceptionResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
