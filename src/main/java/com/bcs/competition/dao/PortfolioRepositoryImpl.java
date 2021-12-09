package com.bcs.competition.dao;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import net.spy.memcached.MemcachedClient;
import org.springframework.stereotype.Component;

/**
 * @author veshutov
 **/
@Component
@RequiredArgsConstructor
public class PortfolioRepositoryImpl implements PortfolioRepository {
    private final MemcachedClient memcachedClient;

    @Override
    public Optional<Portfolio> findById(String id) {
        return Optional.ofNullable((Portfolio) memcachedClient.get(id));
    }

    @Override
    public void save(Portfolio portfolio) {
        memcachedClient.add(portfolio.getId(), 0, portfolio);
    }
}
