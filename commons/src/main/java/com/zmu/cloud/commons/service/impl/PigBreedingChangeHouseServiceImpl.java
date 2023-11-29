package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigBreedingChangeHouseDTO;
import com.zmu.cloud.commons.dto.PigBreedingChangeHouseDetailDTO;
import com.zmu.cloud.commons.dto.PigStockDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigBreedingChangeHouse;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigBreedingChangeHouseMapper;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.service.PigBreedingChangeHouseService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigBreedingChangeHouseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

/**
 * @author shining
 */
@Service
public class PigBreedingChangeHouseServiceImpl
        extends ServiceImpl<PigBreedingChangeHouseMapper, PigBreedingChangeHouse> implements PigBreedingChangeHouseService {
    @Autowired
    private PigBreedingMapper pigBreedingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void change(PigBreedingChangeHouseDTO pigBreedingChangeHouseDTO) {
        if (CollectionUtils.isEmpty(pigBreedingChangeHouseDTO.getList())) {
            throw new BaseException("请选择需要转舍的种猪");
        }
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        for (PigBreedingChangeHouseDetailDTO pigBreedingChangeHouseDetailDTO : pigBreedingChangeHouseDTO.getList()) {
            PigBreeding pigBreeding = pigBreedingMapper.selectById(pigBreedingChangeHouseDetailDTO.getPigBreedingId());
            //同栏
            if (!ObjectUtils.isEmpty(pigBreeding.getPigHouseColumnsId())
                    && pigBreeding.getPigHouseColumnsId().equals(pigBreedingChangeHouseDetailDTO.getHouseColumnsInId())) {
                continue;
            }
            if (pigBreeding.getPresenceStatus() == 2) {
                throw new BaseException("耳号：" + pigBreeding.getEarNumber() + "种猪已离场");
            }
            //查询种猪的库存
//            PigStockDTO pigStockDTO = pigBreedingMapper.count(pigBreedingChangeHouseDetailDTO.getHouseColumnsInId());
//            if (pigStockDTO.getTotal() + 1 > pigStockDTO.getMaxPerColumns()) {
//                throw new BaseException("耳号：" + pigBreeding.getEarNumber() + "入栏时超过最大存栏数，位置：" + pigStockDTO.getPigHouse() + "-" + pigStockDTO.getPigRows() + "-" + pigStockDTO.getPigColumns());
//            }
            //并添加转舍记录
            PigBreedingChangeHouse build2 = PigBreedingChangeHouse.builder().pigBreedingId(pigBreeding.getId())
                    .updateBy(userId).houseColumnsInId(pigBreedingChangeHouseDetailDTO.getHouseColumnsInId())
                    .changeHouseTime(pigBreedingChangeHouseDTO.getChangeHouseTime())
                    .houseColumnsOutId(pigBreeding.getPigHouseColumnsId()).remark(pigBreedingChangeHouseDTO.getRemark())
                    .operatorId(pigBreedingChangeHouseDTO.getOperatorId()).build();
            baseMapper.insert(build2);
            //修改种猪位置
            pigBreeding.setPigHouseColumnsId(pigBreedingChangeHouseDetailDTO.getHouseColumnsInId());
            pigBreeding.setUpdateBy(userId);
            pigBreedingMapper.updateById(pigBreeding);
        }
    }

    @Override
    public void change(PigBreeding pig) {
        PigBreedingChangeHouse build2 = PigBreedingChangeHouse.builder()
                .pigBreedingId(pig.getId())
                .updateBy(RequestContextUtils.getUserId())
                .houseColumnsInId(null)
                .changeHouseTime(new Date())
                .houseColumnsOutId(pig.getPigHouseColumnsId())
                .remark("猪只调栏，被移出栏位")
                .operatorId(RequestContextUtils.getUserId()).build();
        baseMapper.insert(build2);
    }

    @Override
    public PageInfo<EventPigBreedingChangeHouseVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigBreedingChangeHouseVO> eventPigBreedingChangeHouseVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigBreedingChangeHouseVOS);
    }
}
