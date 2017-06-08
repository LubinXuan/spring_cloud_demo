package me.robin.spring.cloud.tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2017-05-03.
 */
public class IdService {
    private final String idPreFix;

    private final AtomicLong id = new AtomicLong(0);

    public IdService() {
        this.idPreFix = new SimpleDateFormat("yyyyMMddHHmm").format(Calendar.getInstance().getTime());
    }

    public String newId() {
        return idPreFix + "_" + id.incrementAndGet();
    }
}
