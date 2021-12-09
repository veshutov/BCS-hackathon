package com.bcs.competition.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

/**
 * @author veshutov
 **/
@Value
@Builder
public class LimitCostDto {
    String ticker;
    int balance;
    BigDecimal totalCost;
    UUID operationId;
}
