package com.bcs.competition.config;

import com.bcs.competition.integrations.prices.TickerPriceProvider;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author veshutov
 **/
@Slf4j
@Configuration
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class RedisConfig {
    private TickerPriceProvider tickerPriceProvider;

    @Autowired
    public void setTickerPriceProvider(@Lazy TickerPriceProvider tickerPriceProvider) {
        this.tickerPriceProvider = tickerPriceProvider;
    }

    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.addMessageListener((message, pattern) -> {
            String ticker = new String(message.getChannel());
            tickerPriceProvider.refreshTickerPrice(ticker.split(":")[1]);
        }, new PatternTopic("*"));
        return listenerContainer;
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Object> objectSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        objectSerializer.setObjectMapper(
                JsonMapper.builder()
                        .findAndAddModules()
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        .build()
        );

        RedisSerializationContext<String, Object> context =
                RedisSerializationContext.<String, Object>newSerializationContext()
                        .key(RedisSerializer.string())
                        .value(objectSerializer)
                        .hashKey(RedisSerializer.string())
                        .hashValue(objectSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
