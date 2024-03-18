package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class InvalidRatingItemTypeException extends RuntimeException {

    private final String errorCode;

    public InvalidRatingItemTypeException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
