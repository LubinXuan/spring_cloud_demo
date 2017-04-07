package me.robin.repo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by xuanlubin on 2017/4/7.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RepoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepoApplication.class,args);
    }
}
