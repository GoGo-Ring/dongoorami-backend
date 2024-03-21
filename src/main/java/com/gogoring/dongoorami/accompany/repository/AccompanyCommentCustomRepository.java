package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.member.domain.Member;
import org.springframework.data.domain.Slice;

public interface AccompanyCommentCustomRepository {

    Slice<AccompanyComment> findAllByMember(Long cursorId, int size, Member member);
}
