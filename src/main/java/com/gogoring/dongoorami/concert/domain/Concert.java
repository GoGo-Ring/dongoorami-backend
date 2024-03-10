package com.gogoring.dongoorami.concert.domain;

import com.gogoring.dongoorami.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Concert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kopisId;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String startedAt;

    @Column(columnDefinition = "TEXT")
    private String endedAt;

    @Column(columnDefinition = "TEXT")
    private String place;

    @Column(columnDefinition = "TEXT")
    private String cast;

    @Column(columnDefinition = "TEXT")
    private String crew;

    @Column(columnDefinition = "TEXT")
    private String runtime;

    @Column(columnDefinition = "TEXT")
    private String age;

    @Column(columnDefinition = "TEXT")
    private String producer;

    @Column(columnDefinition = "TEXT")
    private String agency;

    @Column(columnDefinition = "TEXT")
    private String host;

    @Column(columnDefinition = "TEXT")
    private String management;

    @Column(columnDefinition = "TEXT")
    private String cost;

    private String poster;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String genre;

    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> introductionImages;

    @Column(columnDefinition = "TEXT")
    private String schedule;

    @Builder
    public Concert(String kopisId, String name, String startedAt, String endedAt, String place,
            String cast, String crew, String runtime, String age, String producer, String agency,
            String host, String management, String cost, String poster, String summary,
            String genre, String status, List<String> introductionImages, String schedule) {
        this.kopisId = kopisId;
        this.name = name;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.place = place;
        this.cast = cast;
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
    }
}
