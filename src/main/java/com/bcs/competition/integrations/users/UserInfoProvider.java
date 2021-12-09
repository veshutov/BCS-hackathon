package com.bcs.competition.integrations.users;

/**
 * @author veshutov
 **/
public interface UserInfoProvider {
    String getClientId(String login);
}
