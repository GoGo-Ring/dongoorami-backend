package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.global.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = tokenProvider.createAccessToken(oAuth2User.getProviderId(),
                (List<GrantedAuthority>) oAuth2User.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(oAuth2User.getProviderId());
        Boolean isFirstLogin = oAuth2User.getMember().getBirthDate() == null;
        String uri = createURI(accessToken, refreshToken, isFirstLogin);

        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private String createURI(String accessToken, String refreshToken, Boolean isFirstLogin) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("accessToken", accessToken);
        queryParams.add("refreshToken", refreshToken);
        queryParams.add("isFirstLogin", isFirstLogin.toString());

        return UriComponentsBuilder
                .newInstance()
                .path("/oauth")
                .queryParams(queryParams)
                .build()
                .toUri().toString();
    }
}
