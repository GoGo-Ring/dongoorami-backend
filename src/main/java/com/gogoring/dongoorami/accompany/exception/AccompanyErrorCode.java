package com.gogoring.dongoorami.accompany.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccompanyErrorCode {
    ACCOMPANY_POST_NOT_FOUND("게시글이 존재하지 않습니다."),
    ONLY_WRITER_CAN_MODIFY("게시글의 작성자만 수정할 수 있습니다."),
    INVALID_ACCOMPANY_PURPOSE_TYPE("유효하지 않은 동행 목적입니다."),
    INVALID_REGION_TYPE("유효하지 않은 지역입니다."),
    INVALID_AGE_RANGE("시작 나이는 종료 나이보다 작거나 같아야 합니다."),
    INCOMPLETE_AGE("시작 나이와 종료 나이 값은 함께 필요로 합니다."),
    ACCOMPANY_POST_COMMENT_NOT_FOUND("게시글에 대한 댓글이 존재하지 않습니다."),
    DUPLICATED_ACCOMPANY_APPLY("이미 신청한 동행 구인글입니다."),
    ACCOMPANY_APPLY_COMMENT_MODIFY_DENIED("동행 신청 댓글은 수정이 불가능 합니다."),
    ACCOMPANY_APPLY_NOT_ALLOWED_FOR_WRITER("작성자는 동행 신청이 불가능 합니다."),
    ALREADY_ENDED_CONCERT("이미 종료된 공연입니다."),
    ACCOMPANY_COMMENT_APPLY_CONFIRM_DENIED("동행 신청 댓글을 통해 동행 확정이 가능합니다."),
    ALREADY_APPLY_CONFIRMED_ACCOMPANY_COMMENT("이미 동행 확정된 동행 신청 댓글입니다."),

    private final String message;
}
