package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.mapper.PigLaborMapper;
import com.zmu.cloud.commons.mapper.PigWeanedTaskMapper;
import com.zmu.cloud.commons.mapper.SysProductionTipsMapper;
import com.zmu.cloud.commons.service.PigWeanedTaskService;
import com.zmu.cloud.commons.vo.PigBreedingStatusVO;
import com.zmu.cloud.commons.vo.PigWeanedTaskListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class PigWeanedTaskServiceImpl extends ServiceImpl<PigWeanedTaskMapper, PigWeanedTask> implements PigWeanedTaskService {
    @Autowired
    private SysProductionTipsMapper sysProductionTipsMapper;
    @Autowired
    private PigBreedingMapper pigBreedingMapper;
    @Autowired
    private PigLaborMapper pigLaborMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add() {
        List<SysProductionTips> sysProductionTips = sysProductionTipsMapper.selectByType(8);
        if (CollectionUtils.isEmpty(sysProductionTips)) {
            log.info("待断奶任务为空");
            return;
        }
        for (SysProductionTips sysProductionTip : sysProductionTips) {
            List<PigBreedingStatusVO> list = pigBreedingMapper.selectByPigStatus(PigBreedingStatusEnum.LACTATION.getStatus(),
                    sysProductionTip.getDays(), sysProductionTip.getCompanyId());
            if (CollectionUtils.isEmpty(list)) {
                log.info("公司：{},待断奶种任务数量为空", sysProductionTip.getCompanyId());
                continue;
            }
            log.info("待断奶种猪的数量为：{},公司id:{}", list.size(), sysProductionTip.getCompanyId());
            for (PigBreedingStatusVO pigBreedingStatusVO : list) {
                //查询最后一次分娩记录
                PigLabor pigLabor = pigLaborMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                if (ObjectUtils.isEmpty(pigLabor)) {
                    log.info("种猪Id:{}没有分娩记录，不能创建断奶任务", pigBreedingStatusVO.getId());
                    continue;
                }
                log.info("待断奶种猪Id:{}", pigBreedingStatusVO.getId());
                //查询是否有断奶任务，如果有断奶任务就不添加新任务了,如果没有的话，就添加一条任务记录
                PigWeanedTask pigWeanedTask = baseMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                if (ObjectUtils.isEmpty(pigWeanedTask)) {
                    pigWeanedTask = new PigWeanedTask();
                    pigWeanedTask.setPigBreedingId(pigBreedingStatusVO.getId());
                    pigWeanedTask.setLaborDate(pigLabor.getLaborDate());
                    pigWeanedTask.setCompanyId(pigBreedingStatusVO.getCompanyId());
                    pigWeanedTask.setPigFarmId(pigBreedingStatusVO.getPigFarmId());
                    pigWeanedTask.setDays(sysProductionTip.getDays());
                    baseMapper.insert(pigWeanedTask);
                    log.info("添加待断奶任务记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                } else {
                    pigWeanedTask.setUpdateTime(DateUtil.date());
                    baseMapper.updateById(pigWeanedTask);
                    log.info("修改待断奶记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                }
            }
        }
    }

    @Override
    public PageInfo<PigWeanedTaskListVO> page(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<PigWeanedTaskListVO> pigWeanedTaskListVOS = baseMapper.page(queryPig);
        return PageInfo.of(pigWeanedTaskListVOS);
    }
}
