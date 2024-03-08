package com.gogoring.dongoorami.concert.exception;

import lombok.Getter;

@Getter
public class ConcertNotFoundException extends RuntimeException {

    private final String errorCode;

    public ConcertNotFoundException(ConcertErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
