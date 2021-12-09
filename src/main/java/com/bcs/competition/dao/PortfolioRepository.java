package com.bcs.competition.dao;

import java.util.Optional;

/**
 * @author veshutov
 **/
public interface PortfolioRepository {
    Optional<Portfolio> findById(String id);
    void save(Portfolio portfolio);
}
