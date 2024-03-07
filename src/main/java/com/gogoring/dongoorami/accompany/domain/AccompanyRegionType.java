package com.gogoring.dongoorami.accompany.domain;

import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.InvalidAccompanyRegionTypeException;
import java.util.Arrays;
import java.util.List;

public enum AccompanyRegionType {
    CAPITAL_AREA("수도권(경기, 인천 포함)"),
    GANGWON("강원도"),
    CHUNGCHEONG("충청북도/충청남도"),
    GYEONGSANG("경상북도/경상남도"),
    JEOLLA("전라북도/전라남도"),
    JEJU("제주도");

    String name;

    AccompanyRegionType(String name) {
        this.name = name;
    }

    public static AccompanyRegionType getValue(String name) {
        return Arrays.stream(AccompanyRegionType.values()).filter(
                        regionType -> regionType.getName().equals(name)).findAny()
                .orElseThrow(() -> new InvalidAccompanyRegionTypeException(
                        AccompanyErrorCode.INVALID_REGION_TYPE));
    }

    public static List<String> getNames() {
        return Arrays.stream(AccompanyRegionType.values()).map(AccompanyRegionType::getName)
                .toList();
    }

    public String getName() {
        return name;
    }
}
