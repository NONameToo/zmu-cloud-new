package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigStockDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigGroup;
import com.zmu.cloud.commons.entity.PigPorkChangeHouse;
import com.zmu.cloud.commons.entity.PigPorkStock;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.mapper.PigGroupMapper;
import com.zmu.cloud.commons.mapper.PigPorkChangeHouseMapper;
import com.zmu.cloud.commons.mapper.PigPorkStockMapper;
import com.zmu.cloud.commons.service.PigPorkChangeHouseService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigPorkChangeHouseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author shining
 */
@Service
@Slf4j
public class PigPorkChangeHouseServiceImpl
        extends ServiceImpl<PigPorkChangeHouseMapper, PigPorkChangeHouse> implements PigPorkChangeHouseService {
    @Autowired
    private PigBreedingMapper pigBreedingMapper;
    @Autowired
    private PigPorkStockMapper pigPorkStockMapper;
    @Autowired
    private PigGroupMapper pigGroupMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void change(PigPorkChangeHouse pigPorkChangeHouse) {
        log.info("pigPorkChangeHouse:{}", pigPorkChangeHouse);
        if (pigPorkChangeHouse.getHouseColumnsInId().equals(pigPorkChangeHouse.getHouseColumnsOutId())
                && pigPorkChangeHouse.getPigGroupInId().equals(pigPorkChangeHouse.getPigGroupOutId())) {
            throw new BaseException("同猪栏同猪群不需要转出");
        }
//        PigStockDTO pigStockDTO = pigBreedingMapper.count(pigPorkChangeHouse.getHouseColumnsInId());
//        if (pigStockDTO.getTotal() + pigPorkChangeHouse.getNumber() > pigStockDTO.getMaxPerColumns()) {
//            throw new BaseException("当前存栏数达到最大值,不能转入");
//        }
        //查询转出猪舍下面猪群的数量
        LambdaQueryWrapper<PigPorkStock> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(PigPorkStock::getPigHouseColumnsId, pigPorkChangeHouse.getHouseColumnsOutId());
        queryWrapper2.eq(PigPorkStock::getType, 1);
        queryWrapper2.eq(PigPorkStock::getPigGroupId, pigPorkChangeHouse.getPigGroupOutId());
        PigPorkStock pigPorkStock1 = pigPorkStockMapper.selectOne(queryWrapper2);
        if (ObjectUtils.isEmpty(pigPorkStock1)) {
            throw new BaseException("当前猪栏下没有肉猪");
        }
        if (pigPorkChangeHouse.getNumber() > pigPorkStock1.getPorkNumber()) {
            throw new BaseException("转出的数量大于存栏数");
        }
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        //如果groupId为空就新建一个猪群
        if (ObjectUtils.isEmpty(pigPorkChangeHouse.getPigGroupInId())) {
            if (ObjectUtils.isEmpty(pigPorkChangeHouse.getPigGroupInName())) {
                //如果猪群的名字为空，就自动生成一个,查询今天生成的数量
                Integer count = pigGroupMapper.selectCountByColumnsId(pigPorkChangeHouse.getPigGroupInId());
                String name = DateUtil.date().toString("yyyyMMdd") + "_" + count;
                pigPorkChangeHouse.setPigGroupInName(name);
            }
            PigGroup pigGroup = PigGroup.builder().pigHouseColumnsId(pigPorkChangeHouse.getHouseColumnsInId())
                    .name(pigPorkChangeHouse.getPigGroupInName())
                    .createBy(userId).build();
            pigGroupMapper.insert(pigGroup);
            pigPorkChangeHouse.setPigGroupInId(pigGroup.getId());
        }
        //如果存在，转入的库加
        LambdaQueryWrapper<PigPorkStock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PigPorkStock::getPigHouseColumnsId, pigPorkChangeHouse.getHouseColumnsInId());
        queryWrapper.eq(PigPorkStock::getType, 1);
        queryWrapper.eq(PigPorkStock::getPigGroupId, pigPorkChangeHouse.getPigGroupInId());
        PigPorkStock pigPorkStock2 = pigPorkStockMapper.selectOne(queryWrapper);

        if (ObjectUtils.isEmpty(pigPorkStock2)) {
            PigPorkStock build = PigPorkStock.builder().porkNumber(pigPorkChangeHouse.getNumber())
                    .type(1).pigHouseColumnsId(pigPorkChangeHouse.getHouseColumnsInId())
                    .pigGroupId(pigPorkChangeHouse.getPigGroupInId()).createBy(userId).build();
            pigPorkStockMapper.insert(build);
        } else {
            pigPorkStock2.setPorkNumber(pigPorkStock2.getPorkNumber() + pigPorkChangeHouse.getNumber());
            pigPorkStock2.setUpdateBy(userId);
            pigPorkStockMapper.updateById(pigPorkStock2);
        }
        //转出的库存减去
        pigPorkStock1.setPorkNumber(pigPorkStock1.getPorkNumber() - pigPorkChangeHouse.getNumber());
        pigPorkStock1.setUpdateBy(userId);
        pigPorkStockMapper.updateById(pigPorkStock1);
        //添加转舍记录
        pigPorkChangeHouse.setCreateBy(userId);
        baseMapper.insert(pigPorkChangeHouse);

    }

    @Override
    public PageInfo<EventPigPorkChangeHouseVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigPorkChangeHouseVO> eventPigPorkChangeHouseVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigPorkChangeHouseVOS);
    }
}
