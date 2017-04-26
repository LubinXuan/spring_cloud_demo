package me.robin.spring.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xuanlubin on 2017/4/26.
 */
@RestController
@RequestMapping("/tasks")
public class WxTaskController {

    private static final LinkedBlockingQueue<JSONObject> queue = new LinkedBlockingQueue<>();

    @RequestMapping(method = RequestMethod.GET)
    public JSONObject get() {
        try {
            return queue.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public void post(@RequestParam("numbers") String[] numbers, @RequestParam("message") String message) {
        JSONObject task = new JSONObject();
        task.put("action", "sendMessage");
        task.put("numbers", numbers);
        task.put("message", message);
        queue.offer(task);
    }

}
