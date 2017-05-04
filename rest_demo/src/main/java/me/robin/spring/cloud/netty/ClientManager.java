package me.robin.spring.cloud.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2017-05-03.
 */
@Component
public class ClientManager {

    private Map<String, ChannelWrap> channelMap = new ConcurrentHashMap<>();

    private BlockingQueue<ChannelWrap> idleClientQueue = new LinkedBlockingQueue<>();

    public void registerClient(String clientId, Channel channel) {
        ChannelWrap channelWrap = new ChannelWrap(clientId, channel);
        this.idleClientQueue.offer(channelWrap);
    }

    public ChannelWrap obtainIdleClient() throws InterruptedException {
        ChannelWrap channelWrap = idleClientQueue.take();
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
            ByteBuf byteBuf = null;
            try {
                byte[] data = null;
                if (!StringUtil.isNullOrEmpty(msg)) {
                    data = msg.getBytes(Charset.forName("utf-8"));
                }
                byteBuf = channel.alloc().buffer();
                byteBuf.retain();
                int contentLength = null != data ? data.length : 0;
                byteBuf.writeInt(5 + contentLength);
                byteBuf.writeByte(CusHeartBeatHandler.CUSTOM_MSG);
                if (null != data) {
                    byteBuf.writeBytes(data, 0, data.length);
                }
                channel.writeAndFlush(byteBuf);
            } finally {
                if (null != byteBuf) {
                    byteBuf.release();
                }
            }
        }

        public String getClientId() {
            return clientId;
        }

        public Channel getChannel() {
            return channel;
        }
    }
}
