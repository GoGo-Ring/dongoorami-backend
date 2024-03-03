package com.gogoring.dongoorami.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    private String name;

    @NotBlank(message = "소개는 공백일 수 없습니다.")
    private String introduction;
}
