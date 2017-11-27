package me.robin.spring.cloud.netty;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Lubin.Xuan on 2017-10-17.
 * {desc}
 */
public abstract class ClientRspHandler {

    private ServerHandler serverHandler;

    public abstract void success(String result);

    public abstract void error(String error);

    public ClientRspHandler setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
        return this;
    }
}
