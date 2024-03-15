package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AlreadyApplyConfirmedAccompanyCommentException extends RuntimeException {

    private final String errorCode;

    public AlreadyApplyConfirmedAccompanyCommentException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
