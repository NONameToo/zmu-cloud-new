package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.FinancialData;
import com.zmu.cloud.commons.entity.FinancialDataType;
import com.zmu.cloud.commons.entity.PigPorkLeave;
import com.zmu.cloud.commons.entity.PigPorkStock;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FinancialDataMapper;
import com.zmu.cloud.commons.mapper.FinancialDataTypeMapper;
import com.zmu.cloud.commons.mapper.PigPorkLeaveMapper;
import com.zmu.cloud.commons.mapper.PigPorkStockMapper;
import com.zmu.cloud.commons.service.PigPorkLeaveService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigPorkLeaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author shining
 */
@Service
public class PigPorkLeaveServiceImpl extends ServiceImpl<PigPorkLeaveMapper, PigPorkLeave> implements PigPorkLeaveService {
    @Autowired
    private PigPorkStockMapper pigPorkStockMapper;
    @Autowired
    private FinancialDataTypeMapper financialDataTypeMapper;
    @Autowired
    private FinancialDataMapper financialDataMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leave(PigPorkLeave pigPorkLeave) {
        LambdaQueryWrapper<PigPorkStock> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq(PigPorkStock::getPigHouseColumnsId, pigPorkLeave.getPigHouseColumnsId());
        queryWrapper.eq(PigPorkStock::getPigHouseId, pigPorkLeave.getPigHouseId());
        queryWrapper.eq(PigPorkStock::getType, 1);
        queryWrapper.eq(PigPorkStock::getPigGroupId, pigPorkLeave.getPigGroupId());
        PigPorkStock pigPorkStock = pigPorkStockMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(pigPorkStock)) {
            throw new BaseException("当前猪舍没有肉猪");
        }
        //库存不足
        if (pigPorkLeave.getNumber() > pigPorkStock.getPorkNumber()) {
            throw new BaseException("离场数大于肉猪数");
        }
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        //将原来的库存减去，加入离场库存
        pigPorkStock.setPorkNumber(pigPorkStock.getPorkNumber() - pigPorkLeave.getNumber());
        pigPorkStock.setUpdateBy(userId);
        pigPorkStockMapper.updateById(pigPorkStock);
        //查询离场库存
        LambdaQueryWrapper<PigPorkStock> queryWrapper3 = new LambdaQueryWrapper<>();
        //queryWrapper3.eq(PigPorkStock::getPigHouseColumnsId, pigPorkLeave.getPigHouseColumnsId());
        queryWrapper3.eq(PigPorkStock::getPigHouseId, pigPorkLeave.getPigHouseId());
        queryWrapper3.eq(PigPorkStock::getType, 2);
        queryWrapper3.eq(PigPorkStock::getPigGroupId, pigPorkLeave.getPigGroupId());
        pigPorkStock = pigPorkStockMapper.selectOne(queryWrapper3);
        if (ObjectUtils.isEmpty(pigPorkStock)) {
            PigPorkStock build = PigPorkStock.builder()
                    .porkNumber(pigPorkLeave.getNumber()).type(2).createBy(userId).
                            pigGroupId(pigPorkLeave.getPigGroupId())
                    //.pigHouseColumnsId(pigPorkLeave.getPigHouseColumnsId())
                    .pigHouseId(pigPorkLeave.getPigHouseId())
                    .birthDate(pigPorkLeave.getBirthDate())
                    .build();
            pigPorkStockMapper.insert(build);
        } else {
            pigPorkStock.setPorkNumber(pigPorkStock.getPorkNumber() + pigPorkLeave.getNumber());
            pigPorkStock.setUpdateBy(userId);
            pigPorkStockMapper.updateById(pigPorkStock);
        }
        //添加肉猪离场记录
        pigPorkLeave.setCreateBy(userId);
        baseMapper.insert(pigPorkLeave);
        //添加财务记录
        if (!ObjectUtils.isEmpty(pigPorkLeave.getPrice()) &&
                pigPorkLeave.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            //查询财务类型为出售的Id
            LambdaQueryWrapper<FinancialDataType> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(FinancialDataType::getDataType, 1);
            FinancialDataType financialDataType = financialDataTypeMapper.selectOne(queryWrapper2);
            //记算单价
            BigDecimal unitPrice = pigPorkLeave.getPrice().divide(BigDecimal.valueOf(pigPorkLeave.getNumber()), 2, RoundingMode.HALF_UP);
            FinancialData build1 = FinancialData.builder().dataTypeId(financialDataType.getId())
                    .income(1).number(pigPorkLeave.getNumber()).unitPrice(unitPrice)
                    .totalPrice(pigPorkLeave.getPrice()).status(0).createBy(userId)
                    .remark("肉猪出售,数量：" + pigPorkLeave.getNumber()).build();
            financialDataMapper.insert(build1);
        }

    }

    @Override
    public PageInfo<EventPigPorkLeaveVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigPorkLeaveVO> eventPigPorkLeaveVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigPorkLeaveVOS);
    }
}
