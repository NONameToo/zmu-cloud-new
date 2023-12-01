package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMating;
import com.zmu.cloud.commons.entity.PigPregnancy;
import com.zmu.cloud.commons.entity.PigPregnancyTask;
import com.zmu.cloud.commons.entity.SysProductionTips;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.PigPregnancyTaskService;
import com.zmu.cloud.commons.vo.PigBreedingStatusVO;
import com.zmu.cloud.commons.vo.PigPregnancyTaskListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * @author shining
 */
@Service
@Slf4j
public class PigPregnancyTaskServiceImpl extends ServiceImpl<PigPregnancyTaskMapper, PigPregnancyTask> implements PigPregnancyTaskService {
    @Autowired
    private SysProductionTipsMapper sysProductionTipsMapper;
    @Autowired
    private PigPregnancyMapper pigPregnancyMapper;
    @Autowired
    private PigBreedingMapper pigBreedingMapper;
    @Autowired
    private PigMatingMapper pigMatingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add() {
        //首次待妊检任务
        List<SysProductionTips> sysProductionTips = sysProductionTipsMapper.selectByType(5);
        if (CollectionUtils.isEmpty(sysProductionTips)) {
            log.info("第一次待妊检任务为空");
            return;
        }
        for (SysProductionTips sysProductionTip : sysProductionTips) {
            List<Integer> pigStatus = Lists.newArrayList(PigBreedingStatusEnum.MATING.getStatus(), PigBreedingStatusEnum.PREGNANCY.getStatus(), PigBreedingStatusEnum.ABORTION.getStatus(), PigBreedingStatusEnum.RETURN.getStatus());
            List<PigBreedingStatusVO> pigBreedingStatusVOS = pigBreedingMapper.selectByListPigStatus(pigStatus, sysProductionTip.getCompanyId());
            if (CollectionUtils.isEmpty(pigBreedingStatusVOS)) {
                log.info("公司：{},第一次待妊检任务数量为空", sysProductionTip.getCompanyId());
                continue;
            }
            log.info("第一次待妊检种猪的数量为：{},公司id:{}", pigBreedingStatusVOS.size(), sysProductionTip.getCompanyId());
            //查询同一胎次中第一次妊检的记录
            for (PigBreedingStatusVO pigBreedingStatusVO : pigBreedingStatusVOS) {
                //如果为空，表示，同一胎次没有妊检过
                List<PigPregnancy> pigPregnancies = pigPregnancyMapper.selectPigBreedingIdAndParity(pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                if (CollectionUtils.isEmpty(pigPregnancies)) {
                    //第一次任检，查询最近的一次配种时间
                    PigMating pigMating = pigMatingMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                    if (ObjectUtils.isEmpty(pigMating)) {
                        log.info("种猪id:{}，没有配种记录。可能是进场", pigBreedingStatusVO.getId());
                        continue;
                    }
                    log.info("第一次妊检种猪Id：{}", pigBreedingStatusVO.getId());
                    //查询配种的时间和今天相差多少天
                    long between = DateUtil.between(pigMating.getMatingDate(), DateUtil.date(), DateUnit.DAY);
                    //如果大于的话就生成记录
                    if (between >= sysProductionTip.getDays()) {
                        PigPregnancyTask pigPregnancyTask = baseMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                        if (ObjectUtils.isEmpty(pigPregnancyTask)) {
                            pigPregnancyTask = PigPregnancyTask.builder().status(1)
                                    .matingDate(pigMating.getMatingDate())
                                    .companyId(pigBreedingStatusVO.getCompanyId())
                                    .pigFarmId(pigBreedingStatusVO.getPigFarmId())
                                    .days(sysProductionTip.getDays())
                                    .pigBreedingId(pigBreedingStatusVO.getId()).build();
                            baseMapper.insert(pigPregnancyTask);
                            log.info("添加第一次待妊检记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                        } else {
                            pigPregnancyTask.setUpdateTime(DateUtil.date());
                            baseMapper.updateById(pigPregnancyTask);
                            log.info("修改第一次待妊检记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                        }
                    }
                }
            }
        }
        //第二次待妊检任务
        sysProductionTips = sysProductionTipsMapper.selectByType(6);
        if (CollectionUtils.isEmpty(sysProductionTips)) {
            log.info("第二次待妊检任务为空");
            return;
        }
        for (SysProductionTips sysProductionTip : sysProductionTips) {
            List<Integer> pigStatus = Lists.newArrayList(PigBreedingStatusEnum.PREGNANCY.getStatus(), PigBreedingStatusEnum.ABORTION.getStatus(), PigBreedingStatusEnum.RETURN.getStatus());
            List<PigBreedingStatusVO> pigBreedingStatusVOS = pigBreedingMapper.selectByListPigStatus(pigStatus, sysProductionTip.getCompanyId());
            if (CollectionUtils.isEmpty(pigBreedingStatusVOS)) {
                log.info("公司：{},第二次待妊检任务数量为空", sysProductionTip.getCompanyId());
                continue;
            }
            log.info("第二次待妊检种猪的数量为：{},公司id:{}", pigBreedingStatusVOS.size(), sysProductionTip.getCompanyId());
            //查询同一胎次中第二次次妊检的记录，查询最近的一次配种时间
            for (PigBreedingStatusVO pigBreedingStatusVO : pigBreedingStatusVOS) {
                List<PigPregnancy> pigPregnancies = pigPregnancyMapper.selectPigBreedingIdAndParity(pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                if (!CollectionUtils.isEmpty(pigPregnancies) && pigPregnancies.size() == 1) {
                    //第二次任检
                    PigMating pigMating = pigMatingMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                    if (ObjectUtils.isEmpty(pigMating)) {
                        log.info("种猪id:{}，没有配种记录。可能是进场", pigMating.getPigBreedingId());
                        continue;
                    }
                    log.info("第二次妊检种猪Id：{}", pigBreedingStatusVO.getId());
                    //查询任检的时间和今天相差多少天
                    long between = DateUtil.between(pigMating.getMatingDate(), DateUtil.date(), DateUnit.DAY);
                    //如果大于的话就生成记录
                    if (between >= sysProductionTip.getDays()) {
                        PigPregnancyTask pigPregnancyTask = baseMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                        if (ObjectUtils.isEmpty(pigPregnancyTask)) {
                            pigPregnancyTask = PigPregnancyTask.builder().matingDate(pigMating.getMatingDate())
                                    .companyId(pigBreedingStatusVO.getCompanyId())
                                    .pigFarmId(pigBreedingStatusVO.getPigFarmId())
                                    .days(sysProductionTip.getDays())
                                    .pigBreedingId(pigBreedingStatusVO.getId()).build();
                            baseMapper.insert(pigPregnancyTask);
                            log.info("添加第二次待妊检记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                        } else {
                            pigPregnancyTask.setUpdateTime(DateUtil.date());
                            baseMapper.updateById(pigPregnancyTask);
                            log.info("修改第二次待妊检记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                        }
                    }
                }
            }
        }
    }

    @Override
    public PageInfo<PigPregnancyTaskListVO> page(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<PigPregnancyTaskListVO> pigPregnancyTaskListVOS = baseMapper.page(queryPig);
        return PageInfo.of(pigPregnancyTaskListVOS);
    }
}
