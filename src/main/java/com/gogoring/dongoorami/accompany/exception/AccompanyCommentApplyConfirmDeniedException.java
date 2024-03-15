package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AccompanyCommentApplyConfirmDeniedException extends RuntimeException {

    private final String errorCode;

    public AccompanyCommentApplyConfirmDeniedException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
