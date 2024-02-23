package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;

public interface AccompanyService {

    void createAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId);
}
