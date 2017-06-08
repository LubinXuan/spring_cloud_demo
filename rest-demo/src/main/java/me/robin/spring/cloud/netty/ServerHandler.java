package me.robin.spring.cloud.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017-05-03.
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends CusHeartBeatHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private static JSONObject login = new JSONObject();

    static {
        login.put("action", "login");
        login.put("account", "18258837523");
        login.put("password", "xlb900907");
    }

    @Resource
    private ClientManager clientManager;

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        logger.info("内容长度:{} 内容类型:{} ByteLength:{}", byteBuf.readInt(), byteBuf.readByte(), byteBuf.readableBytes());
        byteBuf.readBytes(data);
        String content = new String(data);
        this.process(channelHandlerContext, content);
    }

    private void process(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("收到指令:{}", msg);
        if (StringUtils.startsWith(msg, "register:")) {
            String clientId = StringUtils.substringAfter(msg, "register:");
            clientManager.registerClient(clientId, ctx.channel());
        } else if (StringUtils.contains(msg, "result")) {
            JSONObject report = JSON.parseObject(msg);
            logger.info("任务处理结果上报 {}", msg);
            clientManager.releaseIdleClient(report.getString("clientId"), ctx.channel());
        } else if (StringUtils.equals(msg, "account")) {
            NettyUtil.sendMessage(ctx, login.toString());
        }
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        System.err.println("---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        ctx.close();
    }

    /*@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("异常",cause);
    }*/
}
