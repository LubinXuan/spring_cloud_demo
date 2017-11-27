package me.robin.spring.cloud.tasks;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lubin.Xuan on 2017-10-23.
 * {desc}
 */
@Component
@Slf4j
public class ClientWebSocketHandler extends AbstractWebSocketHandler {

    private Map<String, String> deviceBindMap = new ConcurrentHashMap<>();

    @Resource
    private ClientManager clientManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("接收到消息: {}  {}", session.getPrincipal().getName(), message.getPayload());
        try {
            JSONObject data = JSON.parseObject(message.getPayload());
            String account = data.getString("accountMobile");
            String clientId = deviceBindMap.get(account);
            if (null == clientId) {
                clientId = clientManager.obtainIdleClient(10);
                deviceBindMap.put(account, clientId);
            }
            TextMessage _message = new TextMessage("设备获取成功");
            session.sendMessage(_message);
        } catch (Throwable e) {
            TextMessage _message = new TextMessage("设备获取异常:" + e.getLocalizedMessage());
            session.sendMessage(_message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }


    private void sendMessage(Object data, WebSocketSession session) throws IOException {
        TextMessage _message = new TextMessage(JSON.toJSONString(data));
        session.sendMessage(_message);
    }
}
