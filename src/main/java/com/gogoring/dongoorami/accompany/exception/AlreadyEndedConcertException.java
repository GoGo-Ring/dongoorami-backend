package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;

@Getter
public class AlreadyEndedConcertException extends RuntimeException {

    private final String errorCode;

    public AlreadyEndedConcertException(AccompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
