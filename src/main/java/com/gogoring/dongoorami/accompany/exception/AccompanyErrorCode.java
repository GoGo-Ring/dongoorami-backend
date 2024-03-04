package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccompanyErrorCode {
    ACCOMPANY_POST_NOT_FOUND("게시글이 존재하지 않습니다."),
    ONLY_WRITER_CAN_MODIFY("게시글의 작성자만 수정할 수 있습니다."),
    INVALID_ACCOMPANY_PURPOSE_TYPE("유효하지 않은 동행 목적입니다.");

    private final String message;
}
