package com.zmu.cloud.commons.config;

import com.sun.istack.NotNull;
import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaojian
 * @create 2023/10/22 15:25
 * @Description
 */
@Data
public class PullJobDelayTask  implements Delayed {

    private long scheduleTime;
    private Runnable runnable;

    public PullJobDelayTask(long scheduleTime, TimeUnit unit, Runnable runnable) {
        this.scheduleTime = System.currentTimeMillis() + (scheduleTime > 0 ? unit.toMillis(scheduleTime) : 0);
        this.runnable = runnable;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return scheduleTime - System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return (int) (this.scheduleTime - ((PullJobDelayTask) o).scheduleTime);
    }

}
