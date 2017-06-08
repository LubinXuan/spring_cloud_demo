package me.robin.spring.cloud;

import feign.Contract;
import feign.MethodMetadata;
import feign.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static feign.Util.checkState;

/**
 * Created by xuanlubin on 2017/4/7.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }


    @Bean
    public Contract feignContract(){
        return new SpringMvcContract(){
            @Override
            public List<MethodMetadata> parseAndValidatateMetadata(Class<?> targetType) {
                Map<String, MethodMetadata> result = new LinkedHashMap<String, MethodMetadata>();
                for (Method method : targetType.getMethods()) {
                    if (method.getDeclaringClass() == Object.class ||
                            (method.getModifiers() & Modifier.STATIC) != 0 ||
                            Util.isDefault(method)) {
                        continue;
                    }
                    MethodMetadata metadata = parseAndValidateMetadata(targetType, method);
                    checkState(!result.containsKey(metadata.configKey()), "Overrides unsupported: %s",
                            metadata.configKey());
                    result.put(metadata.configKey(), metadata);
                }
                return new ArrayList<MethodMetadata>(result.values());
            }
        };
    }
}
