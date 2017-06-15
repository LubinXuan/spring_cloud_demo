package me.robin.spring.cloud.service;

import com.worken.marketing.channel.api.ChannelSendApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by Administrator on 2017-06-08.
 */
@FeignClient("MESSAGE-SEND-SERVICE")
public interface MessageClient extends ChannelSendApi {
}