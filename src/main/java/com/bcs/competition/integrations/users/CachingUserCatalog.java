package com.bcs.competition.integrations.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author veshutov
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'scaled'")
public class CachingUserCatalog implements UserInfoProvider {
    private final RestTemplate userCatalogClient;
    private final UserInfoCache userInfoCache;

    @Override
    public String getClientId(String login) {
        String cachedClientId = userInfoCache.getClientId(login);
        if (cachedClientId != null) {
            return cachedClientId;
        }
        String clientId = loadClientId(login);
        userInfoCache.putClientId(login, clientId);
        return clientId;
    }

    private String loadClientId(String login) {
        UserDto user = userCatalogClient.getForObject("/login-info/{login}", UserDto.class, login);
        if (user == null || user.getClientId() == null) {
            throw new ResourceNotFoundException("user not found, login: login");
        }
        return user.getClientId();
    }
}
