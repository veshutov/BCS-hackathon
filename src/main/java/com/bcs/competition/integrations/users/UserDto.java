package com.bcs.competition.integrations.users;

import lombok.Builder;
import lombok.Value;

/**
 * @author veshutov
 **/
@Value
@Builder
public class UserDto {
    String clientId;
    String name;
    String address;
}
