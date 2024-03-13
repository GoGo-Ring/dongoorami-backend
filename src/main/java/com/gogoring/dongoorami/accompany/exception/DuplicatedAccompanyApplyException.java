package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class DuplicatedAccompanyApplyException extends RuntimeException {

    private final String errorCode;

    public DuplicatedAccompanyApplyException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
