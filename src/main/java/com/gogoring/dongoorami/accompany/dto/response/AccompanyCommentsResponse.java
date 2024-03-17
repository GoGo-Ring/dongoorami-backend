package com.gogoring.dongoorami.accompany.dto.response;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccompanyCommentsResponse {

    private List<AccompanyCommentInfo> accompanyCommentInfos;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class AccompanyCommentInfo {

        private Long id;
        private MemberProfile memberProfile;
        private String content;
        private Boolean isAccompanyApplyComment;
        private Boolean isAccompanyConfirmedComment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static AccompanyCommentInfo of(AccompanyComment accompanyComment,
                Long currentMemberId) {
            return AccompanyCommentInfo.builder()
                    .id(accompanyComment.getId())
                    .memberProfile(MemberProfile.of(accompanyComment.getMember(), currentMemberId))
                    .content(accompanyComment.getContent())
                    .isAccompanyApplyComment(accompanyComment.getIsAccompanyApplyComment())
                    .isAccompanyConfirmedComment(accompanyComment.getIsAccompanyConfirmedComment())
                    .createdAt(accompanyComment.getCreatedAt())
                    .updatedAt(accompanyComment.getUpdatedAt())
                    .build();
        }
    }
}
