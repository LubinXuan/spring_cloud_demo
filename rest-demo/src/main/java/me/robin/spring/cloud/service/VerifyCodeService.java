package me.robin.spring.cloud.service;

import lombok.extern.slf4j.Slf4j;
import me.robin.spring.cloud.tasks.ClientManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lubin.Xuan on 2017-10-23.
 * {desc}
 */
@Component
@Slf4j
public class VerifyCodeService {

    @Resource
    private ClientManager clientManager;

    private ExecutorService service = Executors.newFixedThreadPool(3);

    private final Map<String, NotifyHandler> notifyHandlerMap = new ConcurrentHashMap<>();

    public void updateVerifyCode(String clientId, String verifyCode) {



        NotifyHandler handler = this.notifyHandlerMap.remove(clientId);
        if (null != handler) {
            service.execute(() -> handler.notify(clientId, verifyCode));
        }
    }

    public void registerNotifyHandler(String clientId, NotifyHandler notifyHandler) {
        log.info("客户端:{} 正在请求验证码", clientId);
        this.notifyHandlerMap.put(clientId, notifyHandler);
    }

    public interface NotifyHandler {
        void notify(String clientId, String verifyCode);
    }
}
