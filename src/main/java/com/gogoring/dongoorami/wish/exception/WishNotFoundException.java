package com.gogoring.dongoorami.wish.exception;

import lombok.Getter;

@Getter
public class WishNotFoundException extends RuntimeException {

    private final String errorCode;

    public WishNotFoundException(WishErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
