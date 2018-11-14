package me.robin.cloud;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppWhiteList implements EnvironmentAware {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    private String environmentName;

    private Map<String, Set<String>> remoteRegionAppWhitelist = new ConcurrentHashMap<>();


    public Set<String> getWhiteList(String region) {
        if (StringUtils.isBlank(region)) {
            region = "global";
        }
        return remoteRegionAppWhitelist.computeIfAbsent(region, r -> {
            String whiteListKey = getWhiteListKey(r);
            Set<String> whiteList = redisTemplate.opsForSet().members(whiteListKey);
            if (whiteList.isEmpty()) {
                whiteList = redisTemplate.opsForSet().members(getWhiteListKey("global"));
            }
            return whiteList;
        });
    }

    private String getWhiteListKey(String regionName) {
        return "WHITE_LIST:" + environmentName + ":" + regionName;
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (null == environment.getActiveProfiles() || environment.getActiveProfiles().length == 0) {
            environmentName = "default";
        } else {
            environmentName = environment.getActiveProfiles()[0];
        }
    }
}
