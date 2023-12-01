package com.zmu.cloud.commons.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaojian
 * @create 2023/10/22 15:25
 * @Description
 */
@Slf4j
@Data
public class MyThread implements Runnable{

    int count;

    public MyThread(int count) {
        this.count = count;
    }

    @Override
    public void run() {
        log.info(String.valueOf(count));
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
