package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AccompanyPostCommentNotFoundException extends RuntimeException {

    private final String errorCode;

    public AccompanyPostCommentNotFoundException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
