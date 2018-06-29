package me.robin.cloud;

import com.sun.jersey.api.core.ResourceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

@Component
@Lazy
public class EurekaApplicationConf implements BeanPostProcessor {

    @Resource
    private IpResourceFilterFactory IpResourceFilterFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ResourceConfig) {
            ((ResourceConfig) bean).getProperties().put(ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, Collections.singletonList(IpResourceFilterFactory));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
