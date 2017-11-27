package me.robin.spring.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.worken.common.Rsp;
import me.robin.spring.cloud.netty.ClientRspHandler;
import me.robin.spring.cloud.service.VerifyCodeService;
import me.robin.spring.cloud.tasks.TaskQueueManager;
import me.robin.spring.cloud.vo.AddMobileFriend;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

/**
 * Created by xuanlubin on 2017/4/26.
 */
@RestController
@RequestMapping("/tasks")
public class WxTaskController {

    @Resource
    private VerifyCodeService verifyCodeService;

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
    public DeferredResult<Rsp<String>> screenshot(@RequestParam(value = "quality", defaultValue = "50") int quality) {
        JSONObject task = new JSONObject();
        task.put("action", "screenshot");
        task.put("quality", quality);
        DeferredResult<Rsp<String>> deferredResult = new DeferredResult<>();
        ClientRspHandler rspHandler = new ClientRspHandler() {
            @Override
            public void success(String result) {
                deferredResult.setResult(Rsp.success((String) JSONPath.read(result, "base64")));
            }

            @Override
            public void error(String error) {
                deferredResult.setResult(Rsp.error(error));
            }
        };
        task.put(ClientRspHandler.class.getName(), rspHandler);
        TaskQueueManager.INS.offerTask(task);
        return deferredResult;
    }

    @PostMapping("newTask")
    public String newTask(@RequestBody JSONObject object) {
        return TaskQueueManager.INS.offerTask(object);
    }

    @GetMapping("updateVerifyCode")
    public void updateVerifyCode(@RequestParam("clientId") String clientId, @RequestParam("verifyCode") String verifyCode) {
        verifyCodeService.updateVerifyCode(clientId, verifyCode);
    }

}
