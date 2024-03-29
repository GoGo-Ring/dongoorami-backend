package com.gogoring.dongoorami.accompany.dto.request;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.AlreadyEndedConcertException;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AccompanyPostRequest {

    @NotBlank(message = "title은 공백일 수 없습니다.")
    private String title;

    @NotNull(message = "concertId은 공백일 수 없습니다.")
    @Positive(message = "concertId은 양수만 가능합니다.")
    private Long concertId;

    @NotBlank(message = "region은 공백일 수 없습니다.")
    private String region;

    @NotNull(message = "startAge은 공백일 수 없습니다.")
    @Positive(message = "startAge은 양수만 가능합니다.")
    private Long startAge;

    @NotNull(message = "endAge은 공백일 수 없습니다.")
    @Positive(message = "endAge은 양수만 가능합니다.")
    private Long endAge;

    @NotNull(message = "totalPeople은 공백일 수 없습니다.")
    @Positive(message = "totalPeople은 양수만 가능합니다.")
    private Long totalPeople;

    @NotBlank(message = "gender은 공백일 수 없습니다.")
    @Pattern(regexp = "^(남|여|무관)$", message = "gender는 남, 여, 무관 값 중 하나만 입력 가능합니다.")
    private String gender;

    @NotNull(message = "startDate은 공백일 수 없습니다.")
    private LocalDate startDate;

    @NotNull(message = "endDate은 공백일 수 없습니다.")
    private LocalDate endDate;

    @NotBlank(message = "content은 공백일 수 없습니다.")
    private String content;

    @Size(min = 1, message = "purposes는 1개 이상 필요합니다.")
    private List<String> purposes;

    public AccompanyPost toEntity(Concert concert, Member member, List<String> images) {
        checkAlreadyEndedConcert(concert.getEndLocalDate());

        return AccompanyPost.builder()
                .concert(concert)
                .gender(gender)
                .content(content)
                .endAge(endAge)
                .endDate(endDate)
                .title(title)
                .region(region)
                .startDate(startDate)
                .totalPeople(totalPeople)
                .startAge(startAge)
                .writer(member)
                .images(images)
                .purposes(purposes.stream().map(AccompanyPurposeType::getValue).toList())
                .build();
    }

    private void checkAlreadyEndedConcert(LocalDate concertEndDate) {
        if (concertEndDate.isBefore(LocalDate.now())) {
            throw new AlreadyEndedConcertException(AccompanyErrorCode.ALREADY_ENDED_CONCERT);
        }
    }
}