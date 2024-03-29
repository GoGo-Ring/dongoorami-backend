package com.gogoring.dongoorami.global.config;

import com.gogoring.dongoorami.global.jwt.JwtAccessDeniedHandler;
import com.gogoring.dongoorami.global.jwt.JwtAuthenticationEntryPoint;
import com.gogoring.dongoorami.global.jwt.JwtAuthenticationFilter;
import com.gogoring.dongoorami.global.oauth2.CustomOAuth2UserService;
import com.gogoring.dongoorami.global.oauth2.OAuth2LoginSuccessHandler;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(
                        corsConfigurationSource()))
                .csrf(CsrfConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        requests -> requests.requestMatchers("/error", "/favicon.ico", "/docs/**")
                                .permitAll()
                                .requestMatchers("/oauth2/authorization/**",
                                        "/login/oauth2/code/**", "/oauth/**",
                                        "/actuator/**").permitAll()
                                .requestMatchers("/api/v1/members/reissue").permitAll()
                                // 동행 API
                                .requestMatchers(HttpMethod.GET, "/api/v1/accompanies/posts")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/accompanies/posts/{accompanyPostId}").permitAll()
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/accompanies/comments/{accompanyPostId}")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/accompanies/posts/regions").permitAll()
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/accompanies/posts/concerts/{concertId}")
                                .permitAll()
                                // 공연 API
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/concerts/reviews/{concertId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/concerts/{concertId}")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/concerts").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/concerts/images")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/accompanies-concerts")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOriginPatterns = Arrays.asList("http://localhost:3000",
                "https://dongoorami.netlify.app/");
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader(HttpHeaders.LOCATION);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
