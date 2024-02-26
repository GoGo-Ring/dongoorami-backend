package com.gogoring.dongoorami.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {

    @NotBlank(message = "성별은 공백일 수 없습니다.")
    private String gender;

    @NotNull(message = "생년월일은 공백일 수 없습니다.")
    private LocalDate birthDate;

    @NotBlank(message = "소개는 공백일 수 없습니다.")
    private String introduction;
}
