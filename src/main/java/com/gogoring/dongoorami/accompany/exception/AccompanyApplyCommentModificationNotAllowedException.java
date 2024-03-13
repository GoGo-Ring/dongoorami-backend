package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AccompanyApplyCommentModificationNotAllowedException extends RuntimeException {

    private final String errorCode;

    public AccompanyApplyCommentModificationNotAllowedException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
