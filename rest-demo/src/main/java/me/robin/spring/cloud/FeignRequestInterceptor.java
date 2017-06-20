package me.robin.spring.cloud;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Created by Administrator on 2017-06-08.
 */
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("__inner_server_call__", "true");
    }
}
