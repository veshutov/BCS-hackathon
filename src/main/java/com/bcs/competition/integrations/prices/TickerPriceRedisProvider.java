package com.bcs.competition.integrations.prices;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author veshutov
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class TickerPriceRedisProvider implements TickerPriceProvider {
    private static final String PRICE_HASH_KEY = "price";
    private final Cache<String, BigDecimal> tickerPrice = Caffeine.newBuilder()
            .maximumSize(10000)
            .build();

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private ReactiveHashOperations reactiveHashOps;

    @PostConstruct
    private void init() {
        reactiveHashOps = reactiveRedisTemplate.opsForHash();
    }

    @Override
    public BigDecimal getPrice(String ticker) {
        BigDecimal price = tickerPrice.getIfPresent(ticker);
        if (price == null) {
            return new BigDecimal(String.valueOf(reactiveHashOps.get(ticker, PRICE_HASH_KEY).block()));
        }
        return price;
    }

    @Override
    public void refreshTickerPrice(String ticker) {
        BigDecimal newPrice = new BigDecimal(String.valueOf(reactiveHashOps.get(ticker, PRICE_HASH_KEY).block()));
        tickerPrice.put(ticker, newPrice);
    }
}
