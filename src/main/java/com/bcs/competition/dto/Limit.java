package com.bcs.competition.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

/**
 * @author veshutov
 **/
@Value
@Builder
public class Limit implements Serializable {
    String ticker;
    int balance;
    UUID operationId;
    Instant timestamp;

    public boolean doesntNeedUpdate(Instant newLimitTimestamp) {
        return timestamp.isAfter(newLimitTimestamp);
    }
}
