package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.concert.domain.Concert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ConcertInfoResponse {

    private final Long id;
    private final String name;
    private final String place;

    public static ConcertInfoResponse of(Concert concert) {
        return ConcertInfoResponse.builder()
                .id(concert.getId())
                .name(concert.getName())
                .place(concert.getPlace())
                .build();
    }
}
