package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.concert.domain.Concert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertInfoResponse {

    private final Long id;
    private final String name;
    private final String place;

    @Builder
    public ConcertInfoResponse(Long id, String name, String place) {
        this.id = id;
        this.name = name;
        this.place = place;
    }

    public static ConcertInfoResponse of(Concert concert) {
        return ConcertInfoResponse.builder()
                .id(concert.getId())
                .name(concert.getName())
                .place(concert.getPlace())
                .build();
    }
}
