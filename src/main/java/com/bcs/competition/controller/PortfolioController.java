package com.bcs.competition.controller;

import java.util.List;

import com.bcs.competition.dto.LimitCostDto;
import com.bcs.competition.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author veshutov
 **/
@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping("/{login}")
    public List<LimitCostDto> getUserPortfolio(@PathVariable("login") String login) {
        return portfolioService.getUserLimits(login);
    }
}
