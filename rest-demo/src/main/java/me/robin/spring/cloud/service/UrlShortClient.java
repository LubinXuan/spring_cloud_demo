package me.robin.spring.cloud.service;

import com.worken.url.shorter.api.TestApi;
import com.worken.url.shorter.api.UrlShortApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by Administrator on 2017-06-06.
 */
@FeignClient("URL-SHORT-SERVICE")
public interface UrlShortClient extends UrlShortApi,TestApi {
}
