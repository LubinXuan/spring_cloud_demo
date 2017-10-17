package me.robin.spring.cloud.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import me.robin.spring.cloud.tasks.TaskQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2017-05-03.
 */
@Component
@Lazy(false)
public class TaskServer {

    private static final Logger logger = LoggerFactory.getLogger(TaskServer.class);

    @Resource
    private ServerBootstrap b;

    @Resource
    private InetSocketAddress tcpPort;

    @Resource
    private ClientManager clientManager;

    private ChannelFuture serverChannelFuture;

    private Thread taskDispatchThread;

    private boolean shutdown = false;

    @PostConstruct
    private void init() throws InterruptedException {
        this.serverChannelFuture = b.bind(tcpPort).sync();
        this.taskDispatchThread = new Thread(() -> {
            while (!shutdown) {
                try {
                    JSONObject task = TaskQueueManager.INS.taskTask(-1);
                    if (null != task) {
                        try {
                            Channel channel = clientManager.obtainIdleClient();
                            logger.info("下发任务:{} {}", channel.remoteAddress(), task);
                            NettyUtil.sendMessage(channel, task.toJSONString());
                        } catch (InterruptedException e) {
                            TaskQueueManager.INS.returnTask(task);
                            throw e;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.taskDispatchThread.setName("taskDispatchThread");
        this.taskDispatchThread.start();
        logger.info("NettyServer start listen:{}", this.tcpPort.getPort());
    }

    @PreDestroy
    private void destroy() throws InterruptedException {
        serverChannelFuture.channel().closeFuture().sync();
        this.shutdown = true;
        this.taskDispatchThread.interrupt();
        this.taskDispatchThread.join();
    }

}
