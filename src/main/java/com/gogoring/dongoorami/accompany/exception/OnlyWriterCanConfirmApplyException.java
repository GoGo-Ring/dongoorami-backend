package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class OnlyWriterCanConfirmApplyException extends RuntimeException {

    private final String errorCode;

    public OnlyWriterCanConfirmApplyException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
