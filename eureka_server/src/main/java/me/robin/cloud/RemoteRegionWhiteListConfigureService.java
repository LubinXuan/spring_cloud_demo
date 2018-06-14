package me.robin.cloud;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Lazy(false)
public class RemoteRegionWhiteListConfigureService implements EnvironmentAware {

    private static final String GLOBAL_REMOTE_APP_WHITE_LIST = "global";

    private static final Logger logger = LoggerFactory.getLogger(RemoteRegionWhiteListConfigureService.class);

    //@Value()
    private String environmentName;

    @Resource
    private EurekaServerConfigBean eurekaServerConfig;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    private void init() {
        logger.info("开始初始化{}黑名单设置");
        for (Map.Entry<String, String> entry : eurekaServerConfig.getRemoteRegionUrlsWithName().entrySet()) {
            loadAppWhiteListConfig(entry.getKey());
        }
        loadAppWhiteListConfig(GLOBAL_REMOTE_APP_WHITE_LIST);
    }

    private void loadAppWhiteListConfig(String regionName) {
        String whiteListKey = getWhiteListKey(regionName);
        Set<String> list = redisTemplate.opsForSet().members(whiteListKey);
        if (list.isEmpty() && !StringUtils.equals(regionName, GLOBAL_REMOTE_APP_WHITE_LIST)) {
            return;
        }
        if (!eurekaServerConfig.getRemoteRegionAppWhitelist().containsKey(environmentName)) {
            eurekaServerConfig.getRemoteRegionAppWhitelist().put(regionName, new HashSet<>());
        }
        eurekaServerConfig.getRemoteRegionAppWhitelist().get(regionName).addAll(list);
    }

    public void addAppWhiteList(String regionName, String appName) {
        addAppWhiteListRam(regionName, appName);
        redisTemplate.boundSetOps(getWhiteListKey(regionName)).add(appName);
    }

    public Set<String> getAppWhiteList(String regionName) {
        Set<String> appWhiteList = eurekaServerConfig.getRemoteRegionAppWhitelist(regionName);
        if (null == appWhiteList) {
            appWhiteList = eurekaServerConfig.getRemoteRegionAppWhitelist(GLOBAL_REMOTE_APP_WHITE_LIST);
        }
        if (null == appWhiteList) {
            appWhiteList = Collections.emptySet();
        }
        return appWhiteList;
    }

    private void addAppWhiteListRam(String regionName, String appName) {
        if (!eurekaServerConfig.getRemoteRegionAppWhitelist().containsKey(environmentName)) {
            eurekaServerConfig.getRemoteRegionAppWhitelist().put(regionName, new HashSet<>());
        }
        eurekaServerConfig.getRemoteRegionAppWhitelist().get(regionName).add(appName);
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
        init();
    }
}
