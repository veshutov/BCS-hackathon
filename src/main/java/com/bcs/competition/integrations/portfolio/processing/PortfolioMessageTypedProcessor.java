package com.bcs.competition.integrations.portfolio.processing;

import com.bcs.competition.integrations.portfolio.PortfolioMessage;
import com.bcs.competition.integrations.portfolio.PortfolioMessageType;

/**
 * @author veshutov
 **/
public interface PortfolioMessageTypedProcessor {
    void process(PortfolioMessage portfolioMessage);

    PortfolioMessageType getType();
}
