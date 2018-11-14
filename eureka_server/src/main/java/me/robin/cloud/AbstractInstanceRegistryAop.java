package me.robin.cloud;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@Aspect
public class AbstractInstanceRegistryAop {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInstanceRegistryAop.class);

    @Resource
    private AppWhiteList appWhiteList;


    //com.netflix.eureka.registry.AbstractInstanceRegistry.shouldFetchFromRemoteRegistry
    @Around("execution( * com.netflix.eureka.EurekaServerConfig.getRemoteRegionAppWhitelist(String)) || execution( * com.netflix.eureka.DefaultEurekaServerConfig.getRemoteRegionAppWhitelist(String))")
    public Object getRemoteRegionAppWhitelist(ProceedingJoinPoint pjp) throws Throwable {
        Object data = appWhiteList.getWhiteList((String) pjp.getArgs()[0]);
        logger.info("白名单配置[{}]   {}", pjp.getArgs()[0], data);
        return data;
    }

}
