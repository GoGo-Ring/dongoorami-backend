package com.gogoring.dongoorami.accompany.dto.request;

import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.IncompleteAgeException;
import com.gogoring.dongoorami.accompany.exception.InvalidAgeRangeException;
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
        checkStartAgeAndEndAge(startAge, endAge);
        this.startAge = startAge;
        this.endAge = endAge;
        this.totalPeople = totalPeople;
        this.concertPlace = concertPlace;
        this.purposes = purposes;
    }

    private void checkStartAgeAndEndAge(Long startAge, Long endAge) {
        if (startAge == null ^ endAge == null) {
            throw new IncompleteAgeException(AccompanyErrorCode.INCOMPLETE_AGE);
        }
        if (startAge != null && endAge != null && startAge > endAge) {
            throw new InvalidAgeRangeException(AccompanyErrorCode.INVALID_AGE_RANGE);
        }
    }
}
