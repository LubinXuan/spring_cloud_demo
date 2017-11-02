package me.robin.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

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
}
