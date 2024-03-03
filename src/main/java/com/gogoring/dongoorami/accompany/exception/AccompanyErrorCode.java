package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccompanyErrorCode {
    ACCOMPANY_POST_NOT_FOUND("게시글이 존재하지 않습니다.");

    private final String message;
}
