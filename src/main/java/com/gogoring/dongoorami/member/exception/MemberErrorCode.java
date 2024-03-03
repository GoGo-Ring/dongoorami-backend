package com.gogoring.dongoorami.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {
    MEMBER_NOT_FOUND("회원이 존재하지 않습니다."),
    ALREADY_SIGN_UP("이미 기본 정보가 등록된 회원입니다."),
    INVALID_REFRESH_TOKEN("refresh token이 이미 만료되었거나 올바르지 않습니다.");

    private final String message;
}
