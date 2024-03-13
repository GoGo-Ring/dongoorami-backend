package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AccompanyApplyNotAllowedForWriterException extends RuntimeException {

    private final String errorCode;

    public AccompanyApplyNotAllowedForWriterException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
