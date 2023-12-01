package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.PigHouseRowsDTO;
import com.zmu.cloud.commons.entity.BaseEntity;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.entity.PigHouseRows;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigHouseColumnsMapper;
import com.zmu.cloud.commons.mapper.PigHouseMapper;
import com.zmu.cloud.commons.mapper.PigHouseRowsMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.service.PigHouseRowsService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.ViewRowVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@AllArgsConstructor
public class PigHouseRowsServiceImpl extends ServiceImpl<PigHouseRowsMapper, PigHouseRows>
        implements PigHouseRowsService {

    final PigHouseColumnsMapper columnsMapper;
    PigHouseRowsMapper rowsMapper;
    PigHouseMapper pigHouseMapper;
    RedissonClient redis;

    @Override
    public List<PigHouseRows> list(Long houseId, boolean tree) {
        if (!tree) {
            LambdaQueryWrapper<PigHouseRows> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PigHouseRows::getPigHouseId, houseId);
            wrapper.orderByAsc(PigHouseRows::getCode);
            return rowsMapper.selectList(wrapper);
        }
        return baseMapper.listTree(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(PigHouseRowsDTO dto) {
        Long userId = RequestContextUtils.getUserId();
        PigHouse pigHouse = pigHouseMapper.selectById(dto.getPigHouseId());
        if (pigHouse == null) {
            throw new BaseException("猪舍不存在");
        }
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<PigHouseRows>().eq(PigHouseRows::getPigHouseId, dto.getPigHouseId()));
        String code = StringUtils.leftPad(String.valueOf(count + 1), 2, "0");
        PigHouseRows pigHouseRows = BeanUtil.copyProperties(dto, PigHouseRows.class);
        pigHouseRows.setCreateBy(userId);
        //  如果code、position、name 为空则自动生成
        if (StringUtils.isBlank(pigHouseRows.getName())) {
            pigHouseRows.setName("排位-" + code);
        }
        if (StringUtils.isBlank(pigHouseRows.getCode())) {
            pigHouseRows.setCode(code);
        }
        if (StringUtils.isBlank(pigHouseRows.getPosition())) {
            pigHouseRows.setPosition(pigHouse.getCode() + "-" + code);
        }
        save(pigHouseRows);
        pigHouse.setRows(Integer.parseInt(String.valueOf(count + 1L)));
        pigHouseMapper.updateById(pigHouse);
        Long pigHouseRowsId = pigHouseRows.getId();
        List<PigHouseColumns> pigHouseColumnsList = IntStream.range(1, pigHouse.getColumns() + 1)
                .boxed()
                .map(column -> {
                    String columnCode = StringUtils.leftPad(String.valueOf(column), 2, "0");
                    return PigHouseColumns.builder()
                            .pigHouseId(pigHouse.getId())
                            .pigHouseRowsId(pigHouseRowsId)
                            .name("栏位-" + columnCode)
                            .code(columnCode)
                            .no((Integer.parseInt(code) - 1) * pigHouse.getColumns() + column)
                            .position(pigHouse.getCode() + "-" + pigHouseRows.getCode() + "-" + columnCode)
                            .createBy(userId)
                            .build();
                }).collect(Collectors.toList());
        SpringUtil.getBean(PigHouseColumnsService.class).saveBatch(pigHouseColumnsList);
        return pigHouseRowsId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PigHouseRowsDTO dto) {
        PigHouseRows pigHouseRows = BeanUtil.copyProperties(dto, PigHouseRows.class);
        //不允许修改猪舍id
        dto.setPigHouseId(null);
        pigHouseRows.setUpdateBy(RequestContextUtils.getUserId());
        rowsMapper.updateById(pigHouseRows);
        delCache(dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        int count = baseMapper.countPigNumberByRowsIdAndColumnsId(id, null);
        if (count > 0) {
            throw new BaseException("该排位下存在猪只，不允许删除");
        }
        PigHouseRows pigHouseRows = baseMapper.getById(id);
        if(ObjectUtils.isEmpty(pigHouseRows)){
            throw new BaseException("非法操作，排位不存在");
        }
        LambdaUpdateWrapper<PigHouseRows> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PigHouseRows::getUpdateBy, RequestContextUtils.getUserId())
                .set(PigHouseRows::isDel, true)
                .eq(BaseEntity::getId, id);
        update(updateWrapper);

        //同时删除排下栏位
        LambdaUpdateWrapper<PigHouseColumns> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(PigHouseColumns::getUpdateBy, RequestContextUtils.getUserId())
                .set(PigHouseColumns::getDel, true)
                .eq(PigHouseColumns::getPigHouseRowsId, id);
        columnsMapper.update(null, wrapper);

        Long pigHouseId = pigHouseRows.getPigHouseId();
        PigHouse pigHouse = pigHouseMapper.selectById(pigHouseId);
        Long rows = baseMapper.selectCount(new LambdaQueryWrapper<PigHouseRows>().eq(PigHouseRows::getPigHouseId, pigHouseId));
        pigHouse.setRows(Integer.parseInt(rows.toString()));
        pigHouseMapper.updateById(pigHouse);
        delCache(id);
    }

    @Override
    public List<PigHouseRows> saveBatch(List<PigHouseRows> pigHouseRows) {
        if (CollectionUtils.isEmpty(pigHouseRows)) {
            return Collections.emptyList();
        }
        super.saveBatch(pigHouseRows);
        return pigHouseRows;
    }

    @Override
    public PigHouseRows get(Long pigHouseRowsId) {
        return baseMapper.getById(pigHouseRowsId);
    }

    @Override
    public PigHouseRows findByCache(Long rowId) {
        RBucket<PigHouseRows> bucket = redis.getBucket(CacheKey.PreKey+"row:zmu:"+rowId);
        if (bucket.isExists()) {
            return bucket.get();
        }
        PigHouseRows rows = baseMapper.selectById(rowId);
        bucket.set(rows);
        bucket.expire(Duration.ofDays(7));
        return rows;
    }

    void delCache(Long rowId) {
        redis.getBucket(CacheKey.PreKey+"row:zmu:"+rowId).delete();
    }
}
