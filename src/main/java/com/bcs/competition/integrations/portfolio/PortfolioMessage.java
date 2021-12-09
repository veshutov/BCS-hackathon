package com.bcs.competition.integrations.portfolio;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

/**
 * @author veshutov
 **/
@Value
@Builder
public class PortfolioMessage {
    UUID operationId;
    String clientId;
    PortfolioMessageType type;
    List<LimitBalance> limits;
    Instant timestamp;
}
