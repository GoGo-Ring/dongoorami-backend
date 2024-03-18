package com.gogoring.dongoorami.wish.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WishErrorCode {

    WISH_NOT_FOUND("찜 정보가 존재하지 않습니다."),
    ALREADY_WISHED("이미 찜한 동행 구인글입니다.");

    private final String message;
}
