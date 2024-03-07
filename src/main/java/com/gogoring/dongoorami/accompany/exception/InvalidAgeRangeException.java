package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class InvalidAgeRangeException extends RuntimeException {

    private final String errorCode;

    public InvalidAgeRangeException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
