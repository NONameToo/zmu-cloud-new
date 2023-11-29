package com;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.zmu.cloud.admin.TowerEmqHandleApplication;
import com.zmu.cloud.commons.service.PigLaborTaskService;
import com.zmu.cloud.commons.service.PigMatingTaskService;
import com.zmu.cloud.commons.service.PigPregnancyTaskService;
import com.zmu.cloud.commons.service.PigWeanedTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zmu.cloud.commons.redis.CacheKey.Admin.TASK_PREGNANCY;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AdminApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BaseTest {
    @Autowired
    private PigMatingTaskService pigMatingTaskService;
    @Autowired
    private PigPregnancyTaskService pigPregnancyTaskService;
    @Autowired
    private PigLaborTaskService pigLaborTaskService;
    @Autowired
    private PigWeanedTaskService pigWeanedTaskService;
    @Autowired
    private RedissonClient redissonClient;


    @Test
    @Disabled("testMating不运行")
    public void testMating() {
        pigMatingTaskService.add();
    }

    @Test
    @Disabled("testPregnancy不运行")
    public void testPregnancy() {
        int tmp = 10;
        for (int i = 0; i < tmp; i++) {
            new Thread(() -> {
                pregnancy();
            }).start();
        }
    }

    private void pregnancy() {
        RLock rLock = redissonClient.getLock(TASK_PREGNANCY.key);
        try {
            boolean isLock = rLock.tryLock();
            log.info("运行妊娠任务，:{},获取锁：{}", Thread.currentThread().getName(), isLock);
            if (isLock) {
                pigPregnancyTaskService.add();
            }
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("运行妊娠任务，释放锁：{}", Thread.currentThread().getName());
            }
        }
    }

    @Test
    @Disabled("testLabor不运行")
    public void testLabor() {
        pigLaborTaskService.add();
    }

    @Test
    @Disabled("testWeaned不运行")
    public void testWeaned() {
        pigWeanedTaskService.add();
    }

    @Test
    public void testCal() {

    }

    public static void main(String[] args) {
//        JSONObject obj = JSONUtil.readJSONObject(FileUtil.file("com/scan.json"), Charset.defaultCharset());



//        JSONObject yobj = (JSONObject)obj.get("0");
//        Map<Integer, Integer> data = new HashMap<>();
//
//        yobj.forEach((k, v) -> data.put(Integer.parseInt(k), Integer.parseInt(v.toString())));
//        data.
//        System.out.println(data);
//
//
//        System.out.println(obj);
//        Map<String, Map<String, Integer>> data = new HashMap<>();
//        obj.forEach((key, val) -> {
//            Map<String, Integer> detail = new HashMap<>();
//            JSONObject valObj = (JSONObject)val;
//            valObj.forEach((ang, dis) -> {
//                String angRes =(Integer.parseInt(ang)) + "";
//                int diss = (int)dis * 10;
//                detail.put(angRes, diss);
//            });
////            obj.putOpt(key, valObj);
//            data.put(key, detail);
//        });
//        System.out.println(JSONUtil.parse(data));

//        List<String> strs = FileUtil.readLines(FileUtil.file("D:\\IdeaProjects\\zmu-cloud\\admin\\src\\main\\test\\com\\1111.igs"), Charset.defaultCharset())
//                .stream().filter(str -> str.startsWith("116"))
//                .map(str -> str.split(",")[1] + " " + str.split(",")[2] + " " + str.split(",")[3])
//                .collect(Collectors.toList());
//        FileUtil.writeLines(strs, FileUtil.newFile("D:\\IdeaProjects\\zmu-cloud\\admin\\src\\main\\test\\com\\1111.txt"), Charset.defaultCharset());

    }

}
