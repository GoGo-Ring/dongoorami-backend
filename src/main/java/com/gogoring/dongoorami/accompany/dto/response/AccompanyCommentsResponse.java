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
        private MemberInfo memberInfo;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static AccompanyCommentInfo of(AccompanyComment accompanyComment) {
            return AccompanyCommentInfo.builder()
                    .id(accompanyComment.getId())
                    .memberInfo(MemberInfo.of(accompanyComment.getMember()))
                    .content(accompanyComment.getContent())
                    .createdAt(accompanyComment.getCreatedAt())
                    .updatedAt(accompanyComment.getUpdatedAt())
                    .build();
        }
    }
}
