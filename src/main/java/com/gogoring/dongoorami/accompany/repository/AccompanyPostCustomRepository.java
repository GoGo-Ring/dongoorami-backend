package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.member.domain.Member;
import org.springframework.data.domain.Slice;

public interface AccompanyPostCustomRepository {

    Slice<AccompanyPost> findByAccompanyPostFilterRequest(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest);

    Slice<AccompanyPost> findAllByMember(Long cursorId, int size, Member member);
}
