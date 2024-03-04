package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class OnlyWriterCanModifyException extends RuntimeException {

    private final String errorCode;

    public OnlyWriterCanModifyException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
