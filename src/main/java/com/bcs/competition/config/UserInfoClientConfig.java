package com.bcs.competition.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author veshutov
 **/
@Configuration
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class UserInfoClientConfig {

    @Bean
    public RestTemplate userCatalogClient(RestTemplateBuilder builder) {
        return builder
                .rootUri("http://user-catalog:9002")
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .build();
    }
}
