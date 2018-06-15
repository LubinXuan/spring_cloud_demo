package me.robin.spring.cloud;

import com.worken.common.utils.ribbon.EnableEnvRoute;
import me.robin.spring.cloud.filter.HeaderFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * Created by xuanlubin on 2017/4/7.
 */
@EnableZuulProxy
@SpringCloudApplication
@EnableEnvRoute
public class ApiGateway {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateway.class, args);
    }

    @Bean
    public HeaderFilter headerFilter(){
        return new HeaderFilter();
    }
}
