package com;

import com.zmu.cloud.admin.TowerEmqHandleApplication;
import com.zmu.cloud.admin.mqtt.TowerCalculationV2;
import com.zmu.cloud.commons.mapper.FeedTowerLogMapper;
import com.zmu.cloud.commons.mapper.FeedTowerMapper;
import com.zmu.cloud.commons.service.TowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AdminApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@RequiredArgsConstructor
public class TowerTest {

    final TowerCalculationV2 towerCalculationV2;
    final FeedTowerLogMapper feedTowerLogMapper;
    final FeedTowerMapper feedTowerMapper;


    @Test
    @Disabled("磅单校正测试")
    public void test1(){

//        towerService.bdCorrection(488L,8D);


    }



//    @Test
//    @Disabled("测试根据根据设备ID推送")
//    public void test2(){
//        towerCalculationV2.transferHYB(towerCalculationV2.getYHBRequestData(217L));
//
//    }

}
