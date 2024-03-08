package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class InvalidAccompanyRegionTypeException extends RuntimeException {

    private final String errorCode;

    public InvalidAccompanyRegionTypeException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
