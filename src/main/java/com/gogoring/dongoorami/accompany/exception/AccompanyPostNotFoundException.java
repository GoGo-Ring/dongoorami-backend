package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AccompanyPostNotFoundException extends RuntimeException {

    private final String errorCode;

    public AccompanyPostNotFoundException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
