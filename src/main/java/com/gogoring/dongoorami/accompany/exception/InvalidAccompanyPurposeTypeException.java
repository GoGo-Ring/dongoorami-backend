package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class InvalidAccompanyPurposeTypeException extends RuntimeException {

    private final String errorCode;

    public InvalidAccompanyPurposeTypeException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
