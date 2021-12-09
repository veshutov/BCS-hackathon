package com.bcs.competition.dao;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.bcs.competition.dto.Limit;
import lombok.Builder;
import lombok.Value;

/**
 * @author veshutov
 **/
@Value
@Builder
public class Portfolio implements Serializable {
    String id;
    List<Limit> limits;
    Instant snapshotAt;

    public boolean needsSnapshotUpdate(Instant newSnapshotTimestamp) {
        return snapshotAt == null || snapshotAt.isBefore(newSnapshotTimestamp);
    }
}
