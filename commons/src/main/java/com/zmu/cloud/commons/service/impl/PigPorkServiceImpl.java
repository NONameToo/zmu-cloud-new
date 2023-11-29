package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigStockDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.PigPorkService;
import com.zmu.cloud.commons.service.SysProductionTipsService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigPorkListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author shining
 */
@Service
public class PigPorkServiceImpl extends ServiceImpl<PigPorkMapper, PigPork> implements PigPorkService {
    @Autowired
    private PigBreedingMapper pigBreedingMapper;
    @Autowired
    private FinancialDataMapper financialDataMapper;
    @Autowired
    private FinancialDataTypeMapper financialDataTypeMapper;
    @Autowired
    private PigPorkStockMapper pigPorkStockMapper;
    @Autowired
    private PigGroupMapper pigGroupMapper;
    @Autowired
    private SysProductionTipsService tipsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PigPork pigPork) {
//        PigStockDTO pigStockDTO = pigBreedingMapper.count(pigPork.getPigHouseColumnsId());
//        //验证库存
//        if (pigStockDTO.getTotal() + pigPork.getNumber() > pigStockDTO.getMaxPerColumns()) {
//            throw new BaseException("当前存栏数达到最大值");
//        }
        //如果groupId为空就新建一个猪群
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        //如果groupId为空就新建一个猪群
        if (ObjectUtils.isEmpty(pigPork.getPigGroupId())) {
            if (ObjectUtils.isEmpty(pigPork.getPigGroupName())) {
                //如果猪群的名字为空，就自动生成一个,查询今天生成的数量
                //Integer count = pigGroupMapper.selectCountByColumnsId(pigPork.getPigHouseColumnsId());
                Integer count = pigGroupMapper.selectCountByHouseId(pigPork.getPigHouseId());
                String name = DateUtil.date().toString("yyyyMMdd") + "_" + count;
                pigPork.setPigGroupName(name);
            }
            PigGroup pigGroup = PigGroup.builder()
                    //.pigHouseColumnsId(pigPork.getPigHouseColumnsId())
                    .pigHouseId(pigPork.getPigHouseId())
                    .name(pigPork.getPigGroupName())
                    .createBy(userId).build();
            pigGroupMapper.insert(pigGroup);
            pigPork.setPigGroupId(pigGroup.getId());
        }
        //添加肉猪库存数,如果存在，就直接修改在场（type=1）的库存，如果不存在就添加一条记录
        LambdaQueryWrapper<PigPorkStock> queryWrapper2 = new LambdaQueryWrapper<>();
        //queryWrapper2.eq(PigPorkStock::getPigHouseColumnsId, pigPork.getPigHouseColumnsId());
        queryWrapper2.eq(PigPorkStock::getPigHouseId, pigPork.getPigHouseId());
        queryWrapper2.eq(PigPorkStock::getType, 1);
        queryWrapper2.eq(PigPorkStock::getPigGroupId, pigPork.getPigGroupId());
        PigPorkStock pigPorkStock = pigPorkStockMapper.selectOne(queryWrapper2);
        if (ObjectUtils.isEmpty(pigPorkStock)) {
            PigPorkStock build = PigPorkStock.builder()
                    .porkNumber(pigPork.getNumber())
                    .type(1)
                    .createBy(userId)
                    //.pigHouseColumnsId(pigPork.getPigHouseColumnsId())
                    .pigHouseId(pigPork.getPigHouseId())
                    .pigGroupId(pigPork.getPigGroupId())
                    .birthDate(pigPork.getBirthDate()).build();
            pigPorkStockMapper.insert(build);
            pigPork.setStockId(build.getId());
        } else {
            pigPorkStock.setPorkNumber(pigPorkStock.getPorkNumber() + pigPork.getNumber());
            pigPorkStock.setUpdateBy(userId);
            pigPorkStockMapper.updateById(pigPorkStock);
        }
        //添加入场记录
        pigPork.setCreateBy(userId);
        super.baseMapper.insert(pigPork);
        //添加财务
        if (!ObjectUtils.isEmpty(pigPork.getPrice()) && pigPork.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            //查询财务类型为购买的Id
            BigDecimal unitPrice = pigPork.getPrice().divide(BigDecimal.valueOf(pigPork.getNumber()), 2, RoundingMode.HALF_UP);
            LambdaQueryWrapper<FinancialDataType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FinancialDataType::getDataType, 2);
            FinancialDataType financialDataType = financialDataTypeMapper.selectOne(queryWrapper);
            FinancialData build1 = FinancialData.builder().dataTypeId(financialDataType.getId())
                    .income(2).number(pigPork.getNumber()).unitPrice(unitPrice)
                    .totalPrice(pigPork.getPrice()).status(0).createBy(userId)
                    .remark("肉猪购买：数量：" + pigPork.getNumber()).build();
            financialDataMapper.insert(build1);
        }
    }

    @Override
    public PageInfo<EventPigPorkListVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigPorkListVO> eventPigPorkListVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigPorkListVOS);

    }

    /**
     * 获取系统待出栏天数
     * @return
     */
    @Override
    public int getSysDayAge() {
        Long companyId = RequestContextUtils.getRequestInfo().getCompanyId();
        return tipsService.getWantGoOutDays(companyId);
    }

    @Override
    public Integer hogCount() {
        return baseMapper.hogCount();
    }
}
