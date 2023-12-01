package com.zmu.cloud.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmu.cloud.admin.service.ThirdPartyService;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.Liaotagz;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.enums.TowerLogStatusEnum;
import com.zmu.cloud.commons.mapper.FeedTowerMapper;
import com.zmu.cloud.commons.mapper.LiaotagzMapper;
import com.zmu.cloud.commons.mapper.PigFarmMapper;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class ThirdPartyServiceImpl implements ThirdPartyService {

    final DataSource dataSource;
    final LiaotagzMapper liaotagzMapper;
    final JdbcTemplate jdbcTemplate;
    final FeedTowerMapper feedTowerMapper;
    final TowerService towerService;
    final PigFarmMapper pigFarmMapper;
    final RedissonClient redissonClient;


    public List<Liaotagz> searchTowerDataToEquipment() {
        LambdaQueryWrapper<FeedTower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FeedTower::getCompanyId,100002);
        List<FeedTower> feedTowers = feedTowerMapper.selectList(queryWrapper);
        ArrayList<Liaotagz> liaotagzArrayList = new ArrayList<>();
        feedTowers.forEach(oneTower->{
            //查询巨星料塔每个塔的用料补料
            Long todayUse = towerService.getOneTowerTodayUseORAnd(new ArrayList<Long>(){{this.add(oneTower.getId());}}, TowerLogStatusEnum.USE);
            Long todayAdd = towerService.getOneTowerTodayUseORAnd(new ArrayList<Long>(){{this.add(oneTower.getId());}}, TowerLogStatusEnum.ADD);

            PigFarm pigFarm = pigFarmMapper.byId(oneTower.getPigFarmId());
            FeedTower feedTower = feedTowerMapper.selectById(oneTower.getId());

            Liaotagz liaotagz = Liaotagz.builder()
                    .今日加料(new BigDecimal(todayAdd))
                    .今日用料(new BigDecimal(todayUse))
                    .猪场名称(pigFarm.getName())
                    .料塔名称(feedTower.getName())
                    .余料重量(new BigDecimal(oneTower.getResidualWeight()))
                    .余料体积(new BigDecimal(oneTower.getResidualVolume()))
//                    .空腔体积(new BigDecimal((ObjectUtil.isEmpty(oneTower.getInitVolume())?0:oneTower.getInitVolume()) - (ObjectUtil.isEmpty(oneTower.getResidualVolume())?0:oneTower.getResidualVolume())))
                    .空腔体积(new BigDecimal(ObjectUtil.isEmpty(feedTower.getInitVolume())?0:feedTower.getInitVolume()))
                    .密度(new BigDecimal(oneTower.getDensity()))
                    .日期(LocalDateTime.now()).build();
            liaotagzArrayList.add(liaotagz);
        });
        return liaotagzArrayList;
    }

//    @DS("equipment")
//    public void saveToJXEquipment(List<Liaotagz> liaotagzList){
//        liaotagzList.forEach(one->{
//            liaotagzMapper.insert(one);
//        });
//    }

    @Override
    public void sendFeedTowerDataToEquipment() {
        List<Liaotagz> liaotagzs = searchTowerDataToEquipment();
        // 将任务添加到延迟队列，并设置延迟时间为20秒
        RQueue<String> queue = redissonClient.getQueue("myDelayedQueue");
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(queue);
        delayedQueue.offer("您的任务数据", 20, TimeUnit.SECONDS);
        saveToJXEquipment(liaotagzs);
    }


    @DS("equipment")
    public void saveToJXEquipment(List<Liaotagz> liaotagzList) {
        // 将任务添加到延迟队列，并设置延迟时间为20秒
        RQueue<String> queue = redissonClient.getQueue("myDelayedQueue");
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(queue);

        liaotagzList.forEach(one -> {
            liaotagzMapper.insert(one);
            // 在此处添加任务到延迟队列，延迟20秒执行
            delayedQueue.offer("任务数据", 20, TimeUnit.SECONDS);
        });
    }

    public void executeDelayedTasks() {
        // 这个方法用来执行延迟队列中的任务，需要在后台定时调用
        RQueue<String> queue = redissonClient.getQueue("myDelayedQueue");
        while (true) {
            String taskData = queue.poll();
            if (taskData != null) {
                // 执行任务，这里可以添加您的任务处理逻辑
                // 例如：处理任务数据 taskData
            }
        }
    }



}
