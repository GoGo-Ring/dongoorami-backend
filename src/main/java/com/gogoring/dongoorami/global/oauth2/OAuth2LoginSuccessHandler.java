package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.global.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = tokenProvider.createAccessToken(oAuth2User.getProviderId(),
                (List<GrantedAuthority>) oAuth2User.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(oAuth2User.getProviderId());
        boolean isFirstLogin = oAuth2User.getMember().getBirthDate() == null;

        accessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/oauth");
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/oauth");
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);

        Cookie isFirstLoginCookie = new Cookie("isFirstLogin", Boolean.toString(isFirstLogin));
        isFirstLoginCookie.setPath("/oauth");
        isFirstLoginCookie.setHttpOnly(true);
        response.addCookie(isFirstLoginCookie);

        String uri = createURI();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private String createURI() {
        return UriComponentsBuilder
                .newInstance()
                .path("/oauth")
                .build()
                .toUri().toString();
    }
}
