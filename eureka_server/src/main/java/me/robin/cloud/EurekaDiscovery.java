package me.robin.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EurekaConstants;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;

/**
 * Created by xuanlubin on 2017/4/7.
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaDiscovery {
    public static void main(String[] args) {
        if (args.length > 0) {
            System.setProperty("spring.profiles.active", args[0]);
        }
        SpringApplication.run(EurekaDiscovery.class, args);
    }


    @Bean
    public FilterRegistrationBean requestFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestFilter());
        registration.addUrlPatterns(EurekaConstants.DEFAULT_PREFIX + "/*");
        registration.setName("RequestFilter");
        registration.setOrder(1);
        return registration;
    }
}
