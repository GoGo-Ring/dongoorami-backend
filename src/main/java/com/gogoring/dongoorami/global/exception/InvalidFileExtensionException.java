package com.gogoring.dongoorami.global.exception;

import lombok.Getter;

@Getter
public class InvalidFileExtensionException extends RuntimeException {

    private final String errorCode;

    public InvalidFileExtensionException(GlobalErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
