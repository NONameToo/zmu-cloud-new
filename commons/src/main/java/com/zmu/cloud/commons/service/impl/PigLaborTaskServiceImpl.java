package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.PigLaborService;
import com.zmu.cloud.commons.service.PigLaborTaskService;
import com.zmu.cloud.commons.vo.PigBreedingStatusVO;
import com.zmu.cloud.commons.vo.PigLaborTaskListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author shining
 */
@Service
@Slf4j
public class PigLaborTaskServiceImpl extends ServiceImpl<PigLaborTaskMapper, PigLaborTask> implements PigLaborTaskService {
    @Autowired
    private SysProductionTipsMapper sysProductionTipsMapper;
    @Autowired
    private PigBreedingMapper pigBreedingMapper;
    @Autowired
    private PigPregnancyMapper pigPregnancyMapper;
    @Autowired
    private PigLaborMapper laborMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add() {
        List<SysProductionTips> sysProductionTips = sysProductionTipsMapper.selectByType(7);
        if (CollectionUtils.isEmpty(sysProductionTips)) {
            log.info("对不起没有找到待分娩对应的任务");
            return;
        }
        for (SysProductionTips sysProductionTip : sysProductionTips) {
            List<PigBreedingStatusVO> list = pigBreedingMapper.selectByPigStatus(PigBreedingStatusEnum.PREGNANCY.getStatus(),
                    null, sysProductionTip.getCompanyId());
            if (CollectionUtils.isEmpty(list)) {
                log.info("公司：{},待分娩任务数量为空", sysProductionTip.getCompanyId());
                continue;
            }
            log.info("待分娩种猪的数量为：{},公司id:{}", list.size(),sysProductionTip.getCompanyId());
            for (PigBreedingStatusVO pigBreedingStatusVO : list) {
                //查询最近的一次妊检查时间
                PigPregnancy pigPregnancy = pigPregnancyMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                if (ObjectUtils.isEmpty(pigPregnancy)) {
                    log.info("对不起，没有妊检记录");
                    continue;
                }
                if (pigPregnancy.getPregnancyResult() != 1) {
                    log.info("最后一次妊检不为妊娠：{},所以不能分娩", pigBreedingStatusVO.getId());
                    continue;
                }
                log.info("待分娩种猪Id:{}",pigBreedingStatusVO.getId());
                //查询任检的时间和今天相差多少天
                long between = DateUtil.between(pigPregnancy.getMatingDate(), DateUtil.date(), DateUnit.DAY);
                //如果大于的话就生成记录
                if (between >= sysProductionTip.getDays()) {
                    PigLaborTask pigLaborTask = baseMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                    if (ObjectUtils.isEmpty(pigLaborTask)) {
                        pigLaborTask = new PigLaborTask();
                        pigLaborTask.setPigBreedingId(pigBreedingStatusVO.getId());
                        pigLaborTask.setMatingDate(pigPregnancy.getMatingDate());
                        pigLaborTask.setPregnancyDate(pigPregnancy.getPregnancyDate());
                        pigLaborTask.setDays(sysProductionTip.getDays());
                        pigLaborTask.setCompanyId(pigBreedingStatusVO.getCompanyId());
                        pigLaborTask.setPigFarmId(pigBreedingStatusVO.getPigFarmId());
                        baseMapper.insert(pigLaborTask);
                        log.info("添加一条待分娩记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                    } else {
                        pigLaborTask.setUpdateTime(DateUtil.date());
                        baseMapper.updateById(pigLaborTask);
                        log.info("修改待分娩记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                    }
                }
            }
        }
    }

    @Override
    public PageInfo<PigLaborTaskListVO> page(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<PigLaborTaskListVO> pigLaborTaskListVOS = baseMapper.page(queryPig);
        return PageInfo.of(pigLaborTaskListVOS);
    }
}