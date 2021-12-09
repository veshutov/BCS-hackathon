package com.bcs.competition.integrations.portfolio.processing;

import com.bcs.competition.integrations.portfolio.PortfolioMessage;

/**
 * @author veshutov
 **/
public interface PortfolioMessageProcessor {
    void process(PortfolioMessage portfolioMessage);
}
