package com.zmu.cloud.commons.utils;

import com.zmu.cloud.commons.config.PythonProperties;
import com.zmu.cloud.commons.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author fsk
 */
@Slf4j
public class CallPythonScriptUtil {

    /**
     *
     * @param pythonPath
     * @param scriptPath
     * @param method
     * @param outputFilename
     * @param inputParam
     * @return
     */
    public static Double calculateVolume(String pythonPath, String scriptPath, String method, String outputFilename,
                                         String inputParam, ThreadPoolTaskExecutor taskExecutor) throws Exception {
        // 使用ProcessBuilder运行Python脚本
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, inputParam, method, outputFilename);
        processBuilder.redirectErrorStream(true);
        AtomicReference<Double> volume = new AtomicReference<>(null);
        Process process = processBuilder.start();
//        CountDownLatch latch = new CountDownLatch(1);
        // 创建一个新线程来读取Python脚本的输出
        taskExecutor.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("脚本输出:" + line);
                    if (line.startsWith("Volume:")) {
                        String volumeStr = line.replace("Volume:", "").trim();
                        volume.set(Double.parseDouble(volumeStr));
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("计算点云体积出错: ", e);
            }finally {
//                latch.countDown(); // 子线程执行完毕后，释放锁
            }
        });
        // 等待Python脚本执行完成
//        latch.await(); // 等待子线程执行完成
        int exitCode = process.waitFor();
        log.warn("计算点云体积脚本执行: " + (exitCode == 0 ? "成功" : "失败"));
        if (null == volume.get()) {
            throw new BaseException("计算点云体积出错");
        }
        return volume.get();
    }


    public static void main(String[] args) throws Exception {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(600);
        executor.setMaxPoolSize(20);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

            double volume = calculateVolume("D:\\anaconda3\\python.exe", "D:\\算法\\核心代码\\RepareSuanFaForJavaNo3D.py", "1", "D:/算法/点云图/pic1.jpg",
                    "D:\\项目\\zmu-cloud\\admin\\src\\main\\resources\\pointCloudFile\\jixian.json", executor);
            System.out.println(volume);

    }


}
