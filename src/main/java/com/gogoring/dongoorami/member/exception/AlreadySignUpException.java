package com.gogoring.dongoorami.member.exception;

import lombok.Getter;

@Getter
public class AlreadySignUpException extends RuntimeException {

    private final String errorCode;

    public AlreadySignUpException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
