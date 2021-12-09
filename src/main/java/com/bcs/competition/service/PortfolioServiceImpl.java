package com.bcs.competition.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bcs.competition.dao.Portfolio;
import com.bcs.competition.dao.PortfolioRepository;
import com.bcs.competition.dto.LimitCostDto;
import com.bcs.competition.integrations.prices.TickerPriceProvider;
import com.bcs.competition.integrations.users.UserInfoProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * @author veshutov
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class PortfolioServiceImpl implements PortfolioService {
    private final UserInfoProvider userInfoProvider;
    private final PortfolioRepository portfolioRepository;
    private final TickerPriceProvider tickerPriceProvider;

    @Override
    public List<LimitCostDto> getUserLimits(String login) {
        String clientId = userInfoProvider.getClientId(login);
        Optional<Portfolio> portfolio = portfolioRepository.findById(clientId);
        return portfolio
                .map(Portfolio::getLimits)
                .orElse(List.of())
                .stream()
                .map(l -> {
                         BigDecimal limitPrice = tickerPriceProvider.getPrice(l.getTicker());
                         BigDecimal totalCost = limitPrice.multiply(BigDecimal.valueOf(l.getBalance()));
                         return LimitCostDto.builder()
                                 .ticker(l.getTicker())
                                 .balance(l.getBalance())
                                 .totalCost(totalCost)
                                 .operationId(l.getOperationId())
                                 .build();
                     }
                )
                .collect(Collectors.toList());
    }
}
