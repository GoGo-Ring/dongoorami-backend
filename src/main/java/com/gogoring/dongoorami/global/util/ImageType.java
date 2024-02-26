package com.gogoring.dongoorami.global.util;

public enum ImageType {
    MEMBER("member"), ACCOMPANY_POST("accompany-post"), ACCOMPANY_REVIEW("accompany-review");

    private final String name;

    ImageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
