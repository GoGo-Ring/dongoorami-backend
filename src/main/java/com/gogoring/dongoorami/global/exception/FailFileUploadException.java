package com.gogoring.dongoorami.global.exception;

import lombok.Getter;

@Getter
public class FailFileUploadException extends RuntimeException {

    private final String errorCode;

    public FailFileUploadException(GlobalErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
