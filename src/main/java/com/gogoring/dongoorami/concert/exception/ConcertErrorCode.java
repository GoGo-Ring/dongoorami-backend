package com.gogoring.dongoorami.concert.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertErrorCode {

    CONCERT_NOT_FOUND("공연 정보가 존재하지 않습니다."),
    CONCERT_REVIEW_NOT_FOUND("공연 후기 정보가 존재하지 않습니다."),
    CONCERT_REVIEW_MODIFY_DENIED("수정 권한이 없는 공연 후기입니다.");

    private final String message;
}
