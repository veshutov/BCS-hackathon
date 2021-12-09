package com.bcs.competition.service;

import java.util.List;

import com.bcs.competition.dto.LimitCostDto;

/**
 * @author veshutov
 **/
public interface PortfolioService {
    List<LimitCostDto> getUserLimits(String login);
}
