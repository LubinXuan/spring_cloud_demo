package me.robin.spring.cloud.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017-05-03.
 */
@Component
public class ClientManager {

    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private Map<Channel, String> channelIdMap = new ConcurrentHashMap<>();

    private BlockingQueue<String> idleClientQueue = new LinkedBlockingQueue<>();

    public void registerClient(String clientId, Channel channel) {
        this.idleClientQueue.offer(clientId);
        this.channelMap.put(clientId, channel);
        this.channelIdMap.put(channel, clientId);
    }

    public void removeClient(Channel channel) {
        String channelId = this.channelIdMap.remove(channel);
        if (StringUtils.isNotBlank(channelId)) {
            this.idleClientQueue.remove(channelId);
            this.channelMap.remove(channelId);
        }
    }

    public Channel obtainIdleClient() throws InterruptedException {
        String channelId = null;
        while (null == channelId) {
            channelId = idleClientQueue.poll(5, TimeUnit.SECONDS);
            if (null == channelId || !channelMap.containsKey(channelId)) {
                logger.info("客户端获取超时");
            }
        }
        return channelMap.get(channelId);
    }

    public void releaseIdleClient(String clientId, Channel channel) {
        this.channelMap.put(clientId, channel);
        this.channelIdMap.put(channel, clientId);
        this.idleClientQueue.offer(clientId);
    }
}
