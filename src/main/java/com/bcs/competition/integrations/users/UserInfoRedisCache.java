package com.bcs.competition.integrations.users;

import lombok.RequiredArgsConstructor;
import net.spy.memcached.MemcachedClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

/**
 * @author veshutov
 **/
@Repository
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class UserInfoRedisCache implements UserInfoCache {
    private final MemcachedClient memcachedClient;

    @Override
    public String getClientId(String login) {
        return (String) memcachedClient.get("userIno:" + login);
    }

    @Override
    public void putClientId(String login, String clientId) {
        memcachedClient.set("userIno:" + login, 0, clientId);
    }
}
