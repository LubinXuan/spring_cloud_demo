package me.robin.spring.cloud.tasks;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2017-05-03.
 */
public enum TaskQueueManager {

    INS;

    private LinkedBlockingQueue<JSONObject> queue = new LinkedBlockingQueue<>();

    private final IdService idService;

    TaskQueueManager() {
        this.idService = new IdService();
    }

    public String offerTask(JSONObject task) {
        String id = idService.newId();
        task.put("_id", id);
        this.queue.offer(task);
        return id;
    }

    public void returnTask(JSONObject task) {
        this.queue.offer(task);
    }

    public JSONObject taskTask(long timeout) throws InterruptedException {
        if (timeout == 0) {
            return this.queue.poll();
        } else if (timeout == -1) {
            return this.queue.take();
        } else {
            return this.queue.poll(timeout, TimeUnit.MILLISECONDS);
        }
    }

}
