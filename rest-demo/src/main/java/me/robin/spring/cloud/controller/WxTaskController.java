package me.robin.spring.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import me.robin.spring.cloud.tasks.TaskQueueManager;
import me.robin.spring.cloud.vo.AddMobileFriend;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xuanlubin on 2017/4/26.
 */
@RestController
@RequestMapping("/tasks")
public class WxTaskController {


    @RequestMapping(path = "sendMessage", method = RequestMethod.POST)
    public String sendMessage(@RequestParam("numbers") String[] numbers, @RequestParam("message") String message, @RequestParam(value = "interval", defaultValue = "50") int interval) {
        JSONObject task = new JSONObject();
        task.put("action", "sendMessage");
        task.put("numbers", numbers);
        task.put("message", message);
        task.put("interval", interval);
        return TaskQueueManager.INS.offerTask(task);
    }


    @RequestMapping(path = "addMobileFriend", method = RequestMethod.POST)
    public String addMobileFriend(@RequestBody AddMobileFriend addMobileFriend) {
        JSONObject task = new JSONObject();
        task.put("action", "addMobileFriend");
        task.put("numbers", addMobileFriend.getNumbers());
        task.put("helloMessage", addMobileFriend.getHelloMessage());
        task.put("interval", addMobileFriend.getInterval());
        return TaskQueueManager.INS.offerTask(task);
    }


    @GetMapping(path = "screenshot")
    public String screenshot() {
        JSONObject task = new JSONObject();
        task.put("action", "screenshot");
        return TaskQueueManager.INS.offerTask(task);
    }

    @PostMapping("newTask")
    public String newTask(@RequestBody JSONObject object){
        return TaskQueueManager.INS.offerTask(object);
    }

}
