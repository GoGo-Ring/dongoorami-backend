package com.gogoring.dongoorami.accompany.dto.request;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
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

    public static AccompanyCommentRequest createAccompanyApplyCommentRequest() {
        return new AccompanyCommentRequest("동행 신청합니다!");
    }

    public AccompanyComment toEntity(AccompanyPost accompanyPost, Member member,
            Boolean isAccompanyApplyComment) {
        return AccompanyComment.builder()
                .accompanyPost(accompanyPost)
                .member(member)
                .content(content)
                .isAccompanyApplyComment(isAccompanyApplyComment)
                .build();
    }
}