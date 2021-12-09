package com.bcs.competition.integrations.users;

/**
 * @author veshutov
 **/
public interface UserInfoCache {

    String getClientId(String login);

    void putClientId(String login, String clientId);

}
