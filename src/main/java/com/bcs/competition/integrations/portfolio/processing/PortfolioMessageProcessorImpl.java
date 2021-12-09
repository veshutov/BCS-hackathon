package com.bcs.competition.integrations.portfolio.processing;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bcs.competition.integrations.portfolio.PortfolioMessage;
import com.bcs.competition.integrations.portfolio.PortfolioMessageType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * @author veshutov
 **/
@Service
@ConditionalOnExpression("'${app.mode}' == 'single'")
public class PortfolioMessageProcessorImpl implements PortfolioMessageProcessor {
    private final Map<PortfolioMessageType, PortfolioMessageTypedProcessor> processorsByType;

    public PortfolioMessageProcessorImpl(List<PortfolioMessageTypedProcessor> portfolioMessageTypedProcessors) {
        this.processorsByType = portfolioMessageTypedProcessors.stream()
                .collect(Collectors.toMap(
                        PortfolioMessageTypedProcessor::getType,
                        Function.identity()
                ));
    }

    @Override
    public void process(PortfolioMessage portfolioMessage) {
        PortfolioMessageType portfolioMessageType = portfolioMessage.getType();
        PortfolioMessageTypedProcessor processor = processorsByType.get(portfolioMessageType);
        if (processor == null) {
            throw new RuntimeException("Received unknown portfolio type: " + portfolioMessageType);
        }
        processor.process(portfolioMessage);
    }
}
