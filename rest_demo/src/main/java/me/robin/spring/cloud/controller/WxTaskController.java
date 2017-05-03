package me.robin.spring.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import me.robin.spring.cloud.tasks.TaskQueueManager;
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


    /*@RequestMapping(method = RequestMethod.GET)
    public JSONObject get() {
        try {
            return TaskQueueManager.INS.taskTask(5000);
        } catch (InterruptedException e) {
            return null;
        }
    }*/

    @RequestMapping(method = RequestMethod.POST)
    public String post(@RequestParam("numbers") String[] numbers, @RequestParam("message") String message, @RequestParam(value = "interval", defaultValue = "50") int interval) {
        JSONObject task = new JSONObject();
        task.put("action", "sendMessage");
        task.put("numbers", numbers);
        task.put("message", message);
        task.put("interval", interval);
        return TaskQueueManager.INS.offerTask(task);
    }

}
