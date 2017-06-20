package me.robin.spring.cloud.service;

import com.worken.wx.api.WxClientApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by Administrator on 2017-06-08.
 */
@FeignClient("WX-SERVICE-SERVICE")
public interface WxClient extends WxClientApi {
}
