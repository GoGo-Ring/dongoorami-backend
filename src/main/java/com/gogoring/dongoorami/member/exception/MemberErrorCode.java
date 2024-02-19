package com.gogoring.dongoorami.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {
    MEMBER_NOT_FOUND("회원이 존재하지 않습니다.");

    private final String message;
}
