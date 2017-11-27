package me.robin.spring.cloud.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import me.robin.spring.cloud.Task;
import me.robin.spring.cloud.service.VerifyCodeService;
import me.robin.spring.cloud.tasks.ClientManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        login.put("password", "Xlb900907");
        //login.put("account", "18958159345");
        //login.put("password", "Xxx123456");
    }

    public ServerHandler() {
        super(false);
    }

    @Resource
    private ClientManager clientManager;

    @Resource
    private VerifyCodeService verifyCodeService;

    private Map<String, ClientRspHandler> msgIdRspHandlerMap = new ConcurrentHashMap<>();

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        logger.info("内容长度:{} 内容类型:{} ByteLength:{}", byteBuf.readInt(), byteBuf.readByte(), byteBuf.readableBytes());
        byteBuf.readBytes(data);
        String content = new String(data);
        this.process(channelHandlerContext, content);
    }

    private void process(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("收到客户端消息:{}", msg);
        String[] arr = msg.split("\n");
        String action = arr[0];
        String clientId = arr[0];
        JSONObject data = JSON.parseObject(arr[2]);
        switch (action) {
            case "ASK_ACCOUNT":
                NettyUtil.sendMessage(ctx, login.toString());
                break;
            case "REGISTER":
                clientManager.registerClient(clientId, ctx.channel());
                break;
            case "REPORT":
                String msgId = data.getString(Task.ID);
                logger.info("任务处理结果上报 {}", msg);
                clientManager.releaseIdleClient(clientId, ctx.channel());
                ClientRspHandler clientRspHandler = null;
                if (StringUtils.isNotBlank(msgId)) {
                    clientRspHandler = this.msgIdRspHandlerMap.remove(msgId);
                }
                if (null != clientRspHandler) {
                    int status = data.getIntValue(Task.STATUS);
                    if (0 == status) {
                        clientRspHandler.success(data.getString(Task.RESULT));
                    } else {
                        clientRspHandler.error(data.getString(Task.ERROR));
                    }
                }
                break;
            case "ASK_VERIFY_CODE":
                JSONObject verifyCodeOut = new JSONObject();
                verifyCodeOut.put("filterAction", data.getString("filterAction"));
                verifyCodeService.registerNotifyHandler(clientId, (clientId1, verifyCode) -> {
                    verifyCodeOut.put("verifyCode", verifyCode);
                    NettyUtil.sendMessage(ctx, verifyCodeOut.toJSONString());
                });
                break;
            case "QUERY_TASK":
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        clientManager.removeClient(ctx.channel());
        ctx.channel().close();
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        logger.debug("连接读超时,关闭客户端连接");
        ctx.channel().close();
    }

    public void registerRspHandler(String messageId, ClientRspHandler rspHandler) {
        rspHandler.setServerHandler(this);
        msgIdRspHandlerMap.put(messageId, rspHandler);
    }

    /*@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("异常",cause);
    }*/
}
