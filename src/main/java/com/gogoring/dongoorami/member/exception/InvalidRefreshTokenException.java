package com.gogoring.dongoorami.member.exception;

import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends RuntimeException {

    private final String errorCode;

    public InvalidRefreshTokenException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
