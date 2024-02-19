package com.gogoring.dongoorami.member.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException {

    private final String errorCode;

    public MemberNotFoundException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }
}
