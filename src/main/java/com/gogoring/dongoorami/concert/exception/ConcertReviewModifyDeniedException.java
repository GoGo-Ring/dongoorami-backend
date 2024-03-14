package com.gogoring.dongoorami.concert.exception;

import lombok.Getter;

@Getter
public class ConcertReviewModifyDeniedException extends RuntimeException {

    private final String errorCode;

    public ConcertReviewModifyDeniedException(ConcertErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
