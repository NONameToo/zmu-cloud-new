package com.zmu.cloud.admin;

import com.github.jaemon.dinger.core.annatations.DingerScan;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Slf4j
@MapperScan(basePackages = {"com.zmu.cloud.commons.mapper"})
@ComponentScan(basePackages = "com.zmu.cloud")
@DingerScan(basePackages = "com.zmu.cloud.commons.dinger")
public class TowerEmqHandleApplication {

  public static void main(String[] args) {
    SpringApplication.run(TowerEmqHandleApplication.class, args);
    log.info("TowerEmqHandleApplication 启动成功！");
  }

}
