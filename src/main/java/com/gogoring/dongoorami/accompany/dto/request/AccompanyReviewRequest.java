package com.gogoring.dongoorami.accompany.dto.request;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
@Builder
public class AccompanyReviewRequest {

    @NotNull(message = "memberId는 공백일 수 없습니다.")
    private Long memberId;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    private String content;

    @Range(min = 1, max = 5, message = "평점은 1 이상 5 이하의 정수입니다.")
    @NotNull(message = "평점은 공백일 수 없습니다.")
    private Integer rating;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<String> ratingItemTypes;
}