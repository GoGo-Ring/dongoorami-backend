package com.gogoring.dongoorami.accompany.dto.request;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccompanyCommentRequest {

    @NotBlank(message = "content는 공백일 수 없습니다.")
    private String content;

    public AccompanyComment toEntity(Member member) {
    public AccompanyComment toEntity(Member member, Boolean isAccompanyApplyComment) {
        return AccompanyComment.builder()
                .member(member)
                .content(content)
                .isAccompanyApplyComment(isAccompanyApplyComment)
                .build();
    }
}