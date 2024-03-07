package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import org.springframework.data.domain.Slice;

public interface AccompanyPostCustomRepository {

    Slice<AccompanyPost> findByAccompanyPostFilterRequest(Long cursorId, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest);

}
