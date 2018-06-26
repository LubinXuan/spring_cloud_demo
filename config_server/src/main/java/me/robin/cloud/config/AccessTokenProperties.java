package me.robin.cloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("spring.cloud.config")
public class AccessTokenProperties {

    private String token;

    private Map<String, String> tokenMap;

    public String getToken(String profile) {
        return tokenMap.getOrDefault(profile, token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, String> getTokenMap() {
        return tokenMap;
    }

    public void setTokenMap(Map<String, String> tokenMap) {
        this.tokenMap = tokenMap;
    }
}
