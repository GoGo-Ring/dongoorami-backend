package com.gogoring.dongoorami.concert.dto.response;

import com.gogoring.dongoorami.concert.domain.Concert;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertGetResponse {

    private final Long id;

    private final String name;

    private final String startedAt;

    private final String endedAt;

    private final String place;

    private final String actor;

    private final String crew;

    private final String runtime;

    private final String age;

    private final String producer;

    private final String agency;

    private final String host;

    private final String management;

    private final String cost;

    private final String poster;

    private final String summary;

    private final String genre;

    private final String status;

    private final List<String> introductionImages;

    private final String schedule;

    private final Integer totalAccompanies;

    private final Integer totalReviews;

    @Builder
    public ConcertGetResponse(Long id, String name, String startedAt, String endedAt, String place,
            String actor, String crew, String runtime, String age, String producer, String agency,
            String host, String management, String cost, String poster, String summary,
            String genre, String status, List<String> introductionImages, String schedule,
            Integer totalAccompanies, Integer totalReviews) {
        this.id = id;
        this.name = name;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.place = place;
        this.actor = actor;
        this.crew = crew;
        this.runtime = runtime;
        this.age = age;
        this.producer = producer;
        this.agency = agency;
        this.host = host;
        this.management = management;
        this.cost = cost;
        this.poster = poster;
        this.summary = summary;
        this.genre = genre;
        this.status = status;
        this.introductionImages = introductionImages;
        this.schedule = schedule;
        this.totalAccompanies = totalAccompanies;
        this.totalReviews = totalReviews;
    }

    public static ConcertGetResponse of(Concert concert, Integer totalAccompanies,
            Integer totalReviews) {
        return ConcertGetResponse.builder()
                .id(concert.getId())
                .name(concert.getName())
                .startedAt(concert.getStartedAt())
                .endedAt(concert.getEndedAt())
                .place(concert.getPlace())
                .actor(concert.getActor())
                .crew(concert.getCrew())
                .runtime(concert.getRuntime())
                .age(concert.getAge())
                .producer(concert.getProducer())
                .agency(concert.getAgency())
                .host(concert.getHost())
                .management(concert.getManagement())
                .cost(concert.getCost())
                .poster(concert.getPoster())
                .summary(concert.getSummary())
                .genre(concert.getGenre())
                .status(concert.getStatus())
                .introductionImages(concert.getIntroductionImages())
                .schedule(concert.getSchedule())
                .totalAccompanies(totalAccompanies)
                .totalReviews(totalReviews)
                .build();
    }
}
