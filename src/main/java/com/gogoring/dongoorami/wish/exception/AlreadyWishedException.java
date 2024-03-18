package com.gogoring.dongoorami.wish.exception;

import lombok.Getter;

@Getter
public class AlreadyWishedException extends RuntimeException {

    private final String errorCode;

    public AlreadyWishedException(WishErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
