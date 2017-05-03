package me.robin.spring.cloud.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017-05-03.
 */
@Component
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Resource
    private ClientManager clientManager;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        if (StringUtils.startsWith("register:", msg)) {
            String clientId = StringUtils.substringAfter("register:", msg);
            clientManager.registerClient(clientId, ctx.channel());
        } else if (StringUtils.contains("result", msg)) {
            JSONObject report = JSON.parseObject(msg);
            logger.info("任务处理结果上报 {}", msg);
            clientManager.releaseIdleClient(report.getString("clientId"),ctx.channel());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Channel is active");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Channel is disconnected");
        super.channelInactive(ctx);
    }
}
