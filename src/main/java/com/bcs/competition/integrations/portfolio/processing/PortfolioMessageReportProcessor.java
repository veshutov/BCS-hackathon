package com.bcs.competition.integrations.portfolio.processing;

import com.bcs.competition.integrations.portfolio.PortfolioMessage;
import com.bcs.competition.integrations.portfolio.PortfolioMessageType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * @author veshutov
 **/
@Service
@ConditionalOnExpression("'${app.mode}' == 'single'")
public class PortfolioMessageReportProcessor implements PortfolioMessageTypedProcessor {
    @Override
    public void process(PortfolioMessage portfolioMessage) {
    }

    @Override
    public PortfolioMessageType getType() {
        return PortfolioMessageType.REPORT;
    }
}
