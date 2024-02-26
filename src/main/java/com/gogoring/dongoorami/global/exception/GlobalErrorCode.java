package com.gogoring.dongoorami.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {
    INVALID_FILE_EXTENSION("지원하지 않는 파일 확장자입니다."),
    FAIL_FILE_UPLOAD("파일 업로드에 실패하였습니다.");

    private final String message;
}
