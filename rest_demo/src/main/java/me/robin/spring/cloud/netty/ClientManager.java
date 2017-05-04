package me.robin.spring.cloud.netty;

import io.netty.channel.Channel;
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

    private Map<String, ChannelWrap> channelMap = new ConcurrentHashMap<>();

    private BlockingQueue<ChannelWrap> idleClientQueue = new LinkedBlockingQueue<>();

    public void registerClient(String clientId, Channel channel) {
        ChannelWrap channelWrap = new ChannelWrap(clientId, channel);
        this.idleClientQueue.offer(channelWrap);
    }

    public ChannelWrap obtainIdleClient() throws InterruptedException {
        ChannelWrap channelWrap = null;
        while (null == channelWrap) {
            channelWrap = idleClientQueue.poll(5, TimeUnit.SECONDS);
            if (null == channelWrap) {
                logger.info("客户端获取超时");
            }
        }
        this.channelMap.put(channelWrap.getClientId(), channelWrap);
        return channelWrap;
    }

    public void releaseIdleClient(String clientId, Channel channel) {
        ChannelWrap channelWrap = new ChannelWrap(clientId, channel);
        this.idleClientQueue.offer(channelWrap);
    }

    public class ChannelWrap {
        private String clientId;
        private Channel channel;

        ChannelWrap(String clientId, Channel channel) {
            this.clientId = clientId;
            this.channel = channel;
        }

        public void sendRequest(String msg) {
            NettyUtil.sendMessage(channel, msg);
        }

        public String getClientId() {
            return clientId;
        }

        public Channel getChannel() {
            return channel;
        }
    }
}
