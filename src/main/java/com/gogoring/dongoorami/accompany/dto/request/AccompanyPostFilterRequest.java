package com.gogoring.dongoorami.accompany.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccompanyPostFilterRequest {

    private final String gender;
    private final String region;
    private final Long startAge;
    private final Long endAge;
    private final Long totalPeople;
    private final String concertPlace;
    private final List<String> purposes;

    @Builder
    public AccompanyPostFilterRequest(String gender, String region, Long startAge, Long endAge,
            Long totalPeople, String concertPlace, List<String> purposes) {
        this.gender = (gender != null && gender.equals("무관")) ? null : gender;
        this.region = region;
        this.startAge = startAge;
        this.endAge = endAge;
        this.totalPeople = totalPeople;
        this.concertPlace = concertPlace;
        this.purposes = purposes;
    }
    }
}
