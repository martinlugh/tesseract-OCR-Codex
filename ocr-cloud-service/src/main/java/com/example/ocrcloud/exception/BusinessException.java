package com.example.ocrcloud.exception;

import com.example.ocrcloud.enums.ErrorCode;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code.name();
    }

    public String getCode() {
        return code;
    }
}
