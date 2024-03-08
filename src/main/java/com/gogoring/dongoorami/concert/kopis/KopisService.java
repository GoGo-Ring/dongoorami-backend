package com.gogoring.dongoorami.concert.kopis;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.exception.ConcertErrorCode;
import com.gogoring.dongoorami.concert.exception.ConcertNotFoundException;
import com.gogoring.dongoorami.concert.repository.ConcertRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KopisService {

    private final ConcertRepository concertRepository;
    private final KopisHttpInterface kopisHttpInterface;

    @Value("${kopis.key}")
    private String serviceKey;

    @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Seoul")
    @Transactional
    public void createConcerts() {
        List<String> kopisIds = getAllFromKopis();
        for (String kopisId : kopisIds) {
            if (!concertRepository.existsByKopisId(kopisId)) {
                try {
                    Concert concert = getByKopisId(kopisId).orElseThrow(
                            () -> new ConcertNotFoundException(ConcertErrorCode.CONCERT_NOT_FOUND));
                    concertRepository.save(concert);
                } catch (HttpClientErrorException | ConcertNotFoundException e) {
                    log.warn(e.getMessage() + " index: " + kopisIds.indexOf(kopisId) + " id: "
                            + kopisId, e);
                }
            }
        }
    }

    private List<String> getAllFromKopis() {
        List<String> kopisIds = new ArrayList<>();

        try {
            int page = 1;
            while (true) {
                String result;
                try {
                    result = kopisHttpInterface.findAll(serviceKey, LocalDate.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMdd")), "20241231", page, 5000, "Y");
                } catch (HttpClientErrorException e) {
                    log.warn(e.getMessage() + " page: " + page, e);
                    result = kopisHttpInterface.findAll(serviceKey, LocalDate.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMdd")), "20241231", page, 5000, "Y");
                }

                if (result != null) {
                    JSONObject jsonObject = XML.toJSONObject(result).getJSONObject("dbs");
                    JSONArray jsonArray = jsonObject.getJSONArray("db");

                    log.info("page: " + page + " length: " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String kopisId = jsonArray.getJSONObject(i).getString("mt20id");
                        kopisIds.add(kopisId);
                    }
                }

                page++;
            }
        } catch (JSONException | HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
        }

        return kopisIds;
    }

    private Optional<Concert> getByKopisId(String kopisId) {
        Concert concert = null;

        try {
            String result;
            try {
                result = kopisHttpInterface.findByKopisId(kopisId, serviceKey, "Y");
            } catch (HttpClientErrorException e) {
                log.warn(e.getMessage() + " kopisId: " + kopisId);
                result = kopisHttpInterface.findByKopisId(kopisId, serviceKey, "Y");
            }

            if (result != null) {
                JSONObject jsonObject = XML.toJSONObject(result).getJSONObject("dbs")
                        .getJSONObject("db");

                String name = jsonObject.get("prfnm").toString();
                String startedAt = jsonObject.get("prfpdfrom").toString();
                String endedAt = jsonObject.get("prfpdto").toString();
                String place = jsonObject.get("fcltynm").toString();
                String cast = jsonObject.get("prfcast").toString();
                String crew = jsonObject.get("prfcrew").toString();
                String runtime = jsonObject.get("prfruntime").toString();
                String age = jsonObject.get("prfage").toString();
                String producer = jsonObject.get("entrpsnmP").toString();
                String agency = jsonObject.get("entrpsnmA").toString();
                String host = jsonObject.get("entrpsnmH").toString();
                String management = jsonObject.get("entrpsnmS").toString();
                String cost = jsonObject.get("pcseguidance").toString();
                String poster = jsonObject.get("poster").toString();
                String summary = jsonObject.get("sty").toString();
                String genre = jsonObject.get("genrenm").toString();
                String status = jsonObject.get("prfstate").toString();
                String schedule = jsonObject.get("dtguidance").toString();

                List<String> introductionImages = new ArrayList<>();
                if (jsonObject.getJSONObject("styurls").get("styurl").getClass()
                        .equals(String.class)) {
                    introductionImages.add(
                            jsonObject.getJSONObject("styurls").get("styurl").toString());
                } else {
                    JSONArray jsonArray = jsonObject.getJSONObject("styurls")
                            .getJSONArray("styurl");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String image = jsonArray.get(i).toString();
                        introductionImages.add(image);
                    }
                }

                concert = Concert.builder()
                        .kopisId(kopisId)
                        .name(name)
                        .startedAt(startedAt)
                        .endedAt(endedAt)
                        .place(place)
                        .cast(cast)
                        .crew(crew)
                        .runtime(runtime)
                        .age(age)
                        .producer(producer)
                        .agency(agency)
                        .host(host)
                        .management(management)
                        .cost(cost)
                        .poster(poster)
                        .summary(summary)
                        .genre(genre)
                        .status(status)
                        .schedule(schedule)
                        .introductionImages(introductionImages)
                        .build();
            }
        } catch (JSONException | HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
        }

        return Optional.ofNullable(concert);
    }
}
