package com.gogoring.dongoorami.global.config;

import com.gogoring.dongoorami.concert.kopis.KopisHttpInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    private static final String KOPIS_BASE_URL = "http://kopis.or.kr/openApi/restful/pblprfr";

    @Bean
    public KopisHttpInterface kopisConfig() {
        RestClient restClient = RestClient.builder()
                .baseUrl(KOPIS_BASE_URL).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                restClientAdapter).build();

        return httpServiceProxyFactory.createClient(KopisHttpInterface.class);
    }
}
