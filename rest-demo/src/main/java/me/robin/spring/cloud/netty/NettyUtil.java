package me.robin.spring.cloud.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;

/**
 * Created by Administrator on 2017-05-04.
 */
public class NettyUtil {
    public static void sendMessage(ChannelHandlerContext ctx, String message) {
        sendMessage(ctx.channel(), message);
    }

    public static void sendMessage(Channel channel, String message) {
        ByteBuf byteBuf = null;
        try {
            byte[] data = null;
            if (StringUtils.isNotBlank(message)) {
                data = message.getBytes(Charset.forName("utf-8"));
            }

            int contentLength = null != data ? data.length : 0;
            byteBuf = channel.alloc().buffer(5 + contentLength);
            byteBuf.retain();
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
}
