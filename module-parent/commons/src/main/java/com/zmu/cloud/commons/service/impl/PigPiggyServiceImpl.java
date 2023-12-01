package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPigPiggy;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PigPiggy;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.mapper.PigPiggyMapper;
import com.zmu.cloud.commons.service.PigPiggyService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PigPiggyListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class PigPiggyServiceImpl extends ServiceImpl<PigPiggyMapper, PigPiggy> implements PigPiggyService {

    final PigBreedingMapper pigBreedingMapper;

    @Override
    public PageInfo<PigPiggyListVO> page(QueryPigPiggy queryPigPiggy) {
        PageHelper.startPage(queryPigPiggy.getPage(), queryPigPiggy.getSize());
        List<PigPiggyListVO> pigPiggyListVOList = baseMapper.selectByList(queryPigPiggy);
        return PageInfo.of(pigPiggyListVOList);
    }

    @Override
    public void transfer(Long id, Long houseId, Integer number) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        PigPiggy piggy = baseMapper.selectById(id);
        if (piggy.getNumber() < number) {
            throw new BaseException("转移数量超过该栋舍现有数量");
        }
        PigPiggy in = baseMapper.selectOne(Wrappers.lambdaQuery(PigPiggy.class).eq(PigPiggy::getPigHouseId, houseId));
        if(houseId.equals(piggy.getPigHouseId())){
            throw new BaseException("转入栋舍为当前栋舍,无需转舍！");
        }
        if (ObjectUtil.isEmpty(in)) {
            in = PigPiggy.builder()
                    .pigHouseId(houseId)
                    .number(number)
                    .createBy(info.getUserId()).build();
        } else {
            in.setNumber(in.getNumber() + number);
        }
        piggy.setNumber(piggy.getNumber() - number);
        baseMapper.updateById(piggy);
        saveOrUpdate(in);
    }

    @Override
    public PigPiggy findByHouse(Long houseId) {
        return baseMapper.selectOne(Wrappers.lambdaQuery(PigPiggy.class)
                .eq(PigPiggy::getPigHouseId, houseId).gt(PigPiggy::getNumber, 0));
    }
}
