package com.gogoring.dongoorami.accompany.domain;

import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.OnlyWriterCanModifyException;
import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AccompanyComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private AccompanyPost accompanyPost;
    @ManyToOne
    private Member member;
    private String content;
    private Boolean isAccompanyApplyComment;

    @Builder
    public AccompanyComment(Member member, String content, Boolean isAccompanyApplyComment) {
        this.member = member;
        this.content = content;
        this.isAccompanyApplyComment = isAccompanyApplyComment;
    }

    public void setAccompanyPost(AccompanyPost accompanyPost) {
        this.accompanyPost = accompanyPost;
    }

    public void updateContent(String content, Long memberId) {
        checkIsWriter(memberId);
        this.content = content;
    }

    public void updateIsActivatedFalse(Long memberId) {
        checkIsWriter(memberId);
        updateIsActivatedFalse();
    }

    private void checkIsWriter(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new OnlyWriterCanModifyException(AccompanyErrorCode.ONLY_WRITER_CAN_MODIFY);
        }
    }
}
