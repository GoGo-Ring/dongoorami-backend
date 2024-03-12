package com.gogoring.dongoorami.concert.exception;

import lombok.Getter;

@Getter
public class ConcertReviewNotFoundException extends RuntimeException {

    private final String errorCode;

    public ConcertReviewNotFoundException(ConcertErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
