package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class IncompleteAgeException extends RuntimeException {

    private final String errorCode;

    public IncompleteAgeException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
