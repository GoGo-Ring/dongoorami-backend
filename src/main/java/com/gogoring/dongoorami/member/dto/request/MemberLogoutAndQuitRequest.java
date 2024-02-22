package com.gogoring.dongoorami.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberLogoutAndQuitRequest {

    @NotBlank(message = "accessToken은 공백일 수 없습니다.")
    private String accessToken;

    @NotBlank(message = "refreshToken은 공백일 수 없습니다.")
    private String refreshToken;
}
