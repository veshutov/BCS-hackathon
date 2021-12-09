package com.bcs.competition.integrations.portfolio.processing;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bcs.competition.dao.Portfolio;
import com.bcs.competition.dao.PortfolioRepository;
import com.bcs.competition.dto.Limit;
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
public class PortfolioMessageSnapshotProcessor implements PortfolioMessageTypedProcessor {
    private final PortfolioRepository portfolioRepository;

    @Override
    public void process(PortfolioMessage portfolioMessage) {
        String clientId = portfolioMessage.getClientId();
        Optional<Portfolio> portfolioO = portfolioRepository.findById(clientId);

        if (portfolioO.isEmpty()) {
            createNewPortfolio(portfolioMessage, clientId);
        } else {
            Portfolio portfolio = portfolioO.get();
            if (portfolio.needsSnapshotUpdate(portfolioMessage.getTimestamp())) {
                updateExistingPortfolio(portfolioMessage, clientId, portfolio);
            }
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
                        .snapshotAt(portfolioMessage.getTimestamp())
                        .build()
        );
    }

    private void updateExistingPortfolio(PortfolioMessage portfolioMessage, String clientId, Portfolio portfolio) {
        if (CollectionUtils.isEmpty(portfolioMessage.getLimits())) {
            portfolioRepository.save(
                    Portfolio.builder()
                            .id(clientId)
                            .limits(List.of())
                            .snapshotAt(portfolioMessage.getTimestamp())
                            .build()
            );
            return;
        }
        List<Limit> resultLimits = mergeLimits(portfolioMessage, portfolio);
        portfolioRepository.save(
                Portfolio.builder()
                        .id(clientId)
                        .limits(resultLimits)
                        .snapshotAt(portfolioMessage.getTimestamp())
                        .build()
        );
    }

    private List<Limit> mergeLimits(PortfolioMessage portfolioMessage, Portfolio portfolio) {
        Map<String, Limit> upToDate = portfolio.getLimits().stream()
                .filter(l -> l.doesntNeedUpdate(portfolioMessage.getTimestamp()))
                .collect(Collectors.toMap(
                        Limit::getTicker,
                        Function.identity()
                ));
        List<Limit> newLimits = portfolioMessage.getLimits().stream()
                .filter(l -> !upToDate.containsKey(l.getTicker()))
                .map(l -> Limit.builder()
                        .ticker(l.getTicker())
                        .balance(l.getBalance())
                        .operationId(portfolioMessage.getOperationId())
                        .timestamp(portfolioMessage.getTimestamp())
                        .build())
                .collect(Collectors.toList());
        return Stream.concat(
                newLimits.stream(),
                upToDate.values().stream()
        ).collect(Collectors.toList());
    }

    @Override
    public PortfolioMessageType getType() {
        return PortfolioMessageType.SNAPSHOT;
    }
}
