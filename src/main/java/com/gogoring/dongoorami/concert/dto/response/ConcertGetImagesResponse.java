package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.concert.domain.Concert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertGetImagesResponse {

    private final Long id;

    private final String imageUrl;

    @Builder
    public ConcertGetImagesResponse(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public static ConcertGetImagesResponse of(Concert concert) {
        return ConcertGetImagesResponse.builder()
                .id(concert.getId())
                .imageUrl(concert.getPoster())
                .build();
    }
}
