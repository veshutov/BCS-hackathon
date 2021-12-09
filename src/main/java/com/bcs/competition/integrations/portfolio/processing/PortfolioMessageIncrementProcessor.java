package com.bcs.competition.integrations.portfolio.processing;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bcs.competition.dao.Portfolio;
import com.bcs.competition.dao.PortfolioRepository;
import com.bcs.competition.dto.Limit;
import com.bcs.competition.integrations.portfolio.LimitBalance;
import com.bcs.competition.integrations.portfolio.PortfolioMessage;
import com.bcs.competition.integrations.portfolio.PortfolioMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author veshutov
 **/
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'single'")
public class PortfolioMessageIncrementProcessor implements PortfolioMessageTypedProcessor {
    private final PortfolioRepository portfolioRepository;

    @Override
    public void process(PortfolioMessage portfolioMessage) {
        if (CollectionUtils.isEmpty(portfolioMessage.getLimits())) {
            return;
        }
        String clientId = portfolioMessage.getClientId();
        Optional<Portfolio> portfolioO = portfolioRepository.findById(clientId);

        if (portfolioO.isEmpty()) {
            createNewPortfolio(portfolioMessage, clientId);
        } else {
            updateExistingPortfolio(portfolioMessage, clientId, portfolioO.get());
        }
    }

    private void createNewPortfolio(PortfolioMessage portfolioMessage, String clientId) {
        List<Limit> limits = portfolioMessage.getLimits().stream()
                .map(l -> Limit.builder()
                        .ticker(l.getTicker())
                        .balance(l.getBalance())
                        .operationId(portfolioMessage.getOperationId())
                        .timestamp(portfolioMessage.getTimestamp())
                        .build()
                )
                .collect(Collectors.toList());
        portfolioRepository.save(
                Portfolio.builder()
                        .id(clientId)
                        .limits(limits)
                        .build()
        );
    }

    private void updateExistingPortfolio(PortfolioMessage portfolioMessage, String clientId, Portfolio portfolio) {
        List<Limit> resultLimits = mergeLimits(portfolioMessage, portfolio);
        portfolioRepository.save(
                Portfolio.builder()
                        .id(clientId)
                        .limits(resultLimits)
                        .build()
        );
    }

    private List<Limit> mergeLimits(PortfolioMessage portfolioMessage, Portfolio portfolio) {
        Map<String, LimitBalance> newLimitBalances = portfolioMessage.getLimits().stream()
                .collect(Collectors.toMap(
                        LimitBalance::getTicker,
                        Function.identity()
                ));

        return portfolio.getLimits().stream()
                .map(l -> {
                    LimitBalance newLimit = newLimitBalances.get(l.getTicker());
                    if (newLimit == null || l.doesntNeedUpdate(portfolioMessage.getTimestamp())) {
                        return l;
                    }
                    return Limit.builder()
                            .ticker(newLimit.getTicker())
                            .balance(newLimit.getBalance())
                            .operationId(portfolioMessage.getOperationId())
                            .timestamp(portfolioMessage.getTimestamp())
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    public PortfolioMessageType getType() {
        return PortfolioMessageType.INCREMENT;
    }
}
