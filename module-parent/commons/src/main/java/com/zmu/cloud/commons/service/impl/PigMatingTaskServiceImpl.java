package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMatingTask;
import com.zmu.cloud.commons.entity.SysProductionTips;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.mapper.PigMatingTaskMapper;
import com.zmu.cloud.commons.mapper.SysProductionTipsMapper;
import com.zmu.cloud.commons.service.PigMatingTaskService;
import com.zmu.cloud.commons.vo.PigBreedingStatusVO;
import com.zmu.cloud.commons.vo.PigMatingTaskListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
public class PigMatingTaskServiceImpl extends ServiceImpl<PigMatingTaskMapper, PigMatingTask> implements PigMatingTaskService {
    @Autowired
    private SysProductionTipsMapper sysProductionTipsMapper;
    @Autowired
    private PigBreedingMapper pigBreedingMapper;

    @Override
    public void add() {
        //后备未配种任务
        addMating(PigBreedingStatusEnum.RESERVE.getStatus(), 9);
        //断奶未配种任务
        addMating(PigBreedingStatusEnum.WEANING.getStatus(), 1);
        //返情未配种
        addMating(PigBreedingStatusEnum.RETURN.getStatus(), 2);
        //流产未配种
        addMating(PigBreedingStatusEnum.ABORTION.getStatus(), 3);
        //阴性未配种
        addMating(PigBreedingStatusEnum.EMPTY.getStatus(), 4);
    }

    @Override
    public PageInfo<PigMatingTaskListVO> page(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<PigMatingTaskListVO> pigMatingTaskListVOS = baseMapper.page(queryPig);
        return PageInfo.of(pigMatingTaskListVOS);
    }

    /**
     * 通过猪的状态，和任务的类型，添加对应的任务
     *
     * @param pigStatus
     * @param type
     */
    private void addMating(Integer pigStatus, Integer type) {
        List<SysProductionTips> sysProductionTips = sysProductionTipsMapper.selectByType(type);
        if (CollectionUtils.isEmpty(sysProductionTips)) {
            log.info("对不起没有找到待配种对应的任务");
            return;
        }
        for (SysProductionTips sysProductionTip : sysProductionTips) {
            List<PigBreedingStatusVO> list = pigBreedingMapper.selectByPigStatus(pigStatus, sysProductionTip.getDays(), sysProductionTip.getCompanyId());
            if (CollectionUtils.isEmpty(list)) {
                log.info("公司：{},待配种任务数量为空", sysProductionTip.getCompanyId());
                continue;
            }
            log.info("待配种种猪的数量为：{}，公司：{}", list.size(), sysProductionTip.getCompanyId());
            for (PigBreedingStatusVO pigBreedingStatusVO : list) {
                log.info("待配种种猪Id:{}", pigBreedingStatusVO.getId());
                //查询是否有配种任务，如果有配种任务就不添加新任务了,如果没有的话，就添加一条任务记录
                PigMatingTask pigMatingTask1 = baseMapper.selectByPigBreedingId(pigBreedingStatusVO.getId());
                if (ObjectUtils.isEmpty(pigMatingTask1)) {
                    pigMatingTask1 = new PigMatingTask();
                    pigMatingTask1.setPigBreedingId(pigBreedingStatusVO.getId());
                    pigMatingTask1.setDays(sysProductionTip.getDays());
                    pigMatingTask1.setCompanyId(pigBreedingStatusVO.getCompanyId());
                    pigMatingTask1.setPigFarmId(pigBreedingStatusVO.getPigFarmId());
                    baseMapper.insert(pigMatingTask1);
                    log.info("添加一条未配种记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                } else {
                    pigMatingTask1.setUpdateTime(DateUtil.date());
                    baseMapper.updateById(pigMatingTask1);
                    log.info("修改未配种记录：种猪Id:{},胎次：{}", pigBreedingStatusVO.getId(), pigBreedingStatusVO.getParity());
                }
            }
        }
    }
}
