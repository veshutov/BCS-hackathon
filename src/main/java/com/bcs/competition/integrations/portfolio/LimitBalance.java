package com.bcs.competition.integrations.portfolio;

import lombok.Builder;
import lombok.Value;

/**
 * @author veshutov
 **/
@Value
@Builder
public class LimitBalance {
    String ticker;
    int balance;
}
