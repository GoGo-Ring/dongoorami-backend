package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AccompanyApplyCommentModifyDeniedException extends RuntimeException {

    private final String errorCode;

    public AccompanyApplyCommentModifyDeniedException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
