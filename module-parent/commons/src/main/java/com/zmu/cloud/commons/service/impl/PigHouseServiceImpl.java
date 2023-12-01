package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigHouseDTO;
import com.zmu.cloud.commons.dto.PigHouseQuery;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.HouseType;
import com.zmu.cloud.commons.enums.HouseTypeForSph;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.MaterialLineMapper;
import com.zmu.cloud.commons.mapper.PigHouseMapper;
import com.zmu.cloud.commons.mapper.PigTypeMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.service.PigHouseRowsService;
import com.zmu.cloud.commons.service.PigHouseService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PigHouseTypeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * 智慧猪家、云慧养共用PigHouse表，巨星猪场数据定时同步到该表中，ID值做预留
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PigHouseServiceImpl extends ServiceImpl<PigHouseMapper, PigHouse> implements PigHouseService {

    final PigTypeMapper pigTypeMapper;
    final PigFarmService farmService;
    final MaterialLineMapper materialLineMapper;
    final PigHouseRowsService pigHouseRowsService;
    final PigHouseColumnsService pigHouseColumnsService;
    final RedissonClient redis;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(PigHouseDTO pigHouseDTO) {
        Long createUserId = RequestContextUtils.getUserId();
        LambdaQueryWrapper<PigHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigHouse::getName, pigHouseDTO.getName());
        PigHouse exists = baseMapper.selectOne(wrapper);
        if(ObjectUtil.isNotEmpty(exists)){
            throw new BaseException("该类型的栋舍名已存在!");
        }
        PigHouse pigHouse = BeanUtil.copyProperties(pigHouseDTO, PigHouse.class);
        pigHouse.setCode(HouseType.getInstance(pigHouseDTO.getType()).getName());
        pigHouse.setCreateBy(createUserId);
        if (ObjectUtil.isEmpty(pigHouseDTO.getPigTypeId())) {
            PigFarm farm = farmService.findByCache(RequestContextUtils.getRequestInfo().getPigFarmId());
            pigHouse.setPigTypeId(farm.getPigTypeId());
        } else if (-1 == pigHouseDTO.getPigTypeId()) {
            if (ObjectUtil.isEmpty(pigHouseDTO.getPigType())) {
                throw new BaseException("请输入猪种名称");
            }
            PigType type = PigType.builder().companyId(RequestContextUtils.getRequestInfo().getCompanyId())
                    .name(pigHouseDTO.getPigType())
                    .createBy(RequestContextUtils.getUserId()).build();
            pigTypeMapper.insert(type);
            pigHouse.setPigTypeId(type.getId());
        }
        save(pigHouse);
        Long pigHouseId = pigHouse.getId();
        Integer rows = pigHouse.getRows();
        Integer columns = pigHouse.getColumns();
        List<PigHouseRows> pigHouseRows = IntStream.range(1, rows + 1).boxed().map(row -> {
            String rowCode = StringUtils.leftPad(String.valueOf(row), 2, "0");
            return PigHouseRows.builder()
                    .pigHouseId(pigHouseId)
                    .code(rowCode)
                    .name("排位-" + rowCode)
                    .position(pigHouse.getCode() + "-" + rowCode)
                    .createBy(createUserId)
                    .build();
        }).collect(toList());
        pigHouseRowsService.saveBatch(pigHouseRows).forEach(pigHouseRow -> {
            List<PigHouseColumns> pigHouseColumnsList = IntStream.range(1, columns + 1)
                    .boxed()
                    .map(column -> {
                        String columnCode = StringUtils.leftPad(String.valueOf(column), 2, "0");
                        return PigHouseColumns.builder()
                                .pigHouseId(pigHouseRow.getPigHouseId())
                                .pigHouseRowsId(pigHouseRow.getId())
                                .name("栏位-" + columnCode)
                                .code(columnCode)
                                .no((Integer.parseInt(pigHouseRow.getCode()) - 1) * columns + column)
                                .position(pigHouse.getCode() + "-" + pigHouseRow.getCode() + "-" + columnCode)
                                .createBy(createUserId)
                                .build();
                    }).collect(toList());
            pigHouseColumnsService.saveBatch(pigHouseColumnsList);
        });
        return pigHouseId;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(PigHouseDTO pigHouseDTO) {
        LambdaQueryWrapper<PigHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigHouse::getName, pigHouseDTO.getName()).ne(PigHouse::getId, pigHouseDTO.getId());
        PigHouse exists = baseMapper.selectOne(wrapper);
        if(ObjectUtil.isNotEmpty(exists)){
            throw new BaseException("该类型的栋舍名已存在!");
        }
        //不能在更新猪舍时同步修改排和栏，只能初始化和通过排和栏的接口修改
        PigHouse pigHouse = BeanUtil.copyProperties(pigHouseDTO, PigHouse.class);
        pigHouse.setUpdateBy(RequestContextUtils.getUserId());
        if (-1 == pigHouseDTO.getPigTypeId()) {
            if (ObjectUtil.isEmpty(pigHouseDTO.getPigType())) {
                throw new BaseException("请输入猪种名称");
            }
            PigType type = PigType.builder().companyId(pigHouse.getCompanyId()).name(pigHouseDTO.getPigType())
                    .createBy(RequestContextUtils.getUserId()).build();
            pigTypeMapper.insert(type);
            pigHouse.setPigTypeId(type.getId());
        }
        delCache(pigHouse.getId());
        return updateById(pigHouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        throw new BaseException("不支持删除猪舍");
    }

    @Override
    public PigHouse get(Long id, boolean tree) {
        if (tree) {
            return baseMapper.getById(id);
        }
        return baseMapper.selectById(id);
    }

    @Override
    public PageInfo<PigHouse> list(PigHouseQuery pigHouseQuery) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        PageHelper.startPage(pigHouseQuery.getPage(), pigHouseQuery.getSize());
        LambdaQueryWrapper<PigHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigHouse::getPigFarmId, info.getPigFarmId());
        if (ObjectUtil.isNotEmpty(pigHouseQuery.getName())) {
            wrapper.like(PigHouse::getName, pigHouseQuery.getName());
        }
        if (ObjectUtil.isNotEmpty(pigHouseQuery.getType())) {
            wrapper.eq(PigHouse::getType, pigHouseQuery.getType());
        }
        wrapper.last("order by find_in_set(name,'一,二,三,四,五,六,七,八,九,十')");

        List<PigHouse> houses = baseMapper.selectList(wrapper);
        if (pigHouseQuery.isJoinPigType()) {
            houses = houses.stream().peek(h -> {
                if (null != h.getPigTypeId()) {
                    h.setPigType(pigTypeMapper.selectById(h.getPigTypeId()).getName());
                }
            }).collect(Collectors.toList());
        }
        return PageInfo.of(houses);
    }

    @Override
    public List<PigHouse> listForPorkLeave() {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        return baseMapper.listAvailableHouseForPork(requestInfo.getCompanyId(), requestInfo.getPigFarmId());
    }

    @Override
    public List<PigHouseTypeVo> houseTypes() {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        if (ResourceType.YHY.equals(info.getResourceType())) {
            return Arrays.stream(HouseType.values())
                    .sorted(Comparator.comparing(HouseType::getSort))
                    .map(t -> PigHouseTypeVo.builder().typeId(t.getType()).typeName(t.getName()).build()).collect(toList());
        } else if (ResourceType.JX.equals(info.getResourceType())) {
            return Arrays.stream(HouseTypeForSph.values())
                    .sorted(Comparator.comparing(HouseTypeForSph::getSort))
                    .map(t -> PigHouseTypeVo.builder().typeId(t.getType()).typeName(t.getName()).build()).collect(toList());
        }
        return ListUtil.empty();
    }

    @Override
    public PigHouse findByCache(Long houseId) {
        RBucket<PigHouse> bucket = redis.getBucket(CacheKey.Web.house.key + houseId);
        if (bucket.isExists()) {
            return bucket.get();
        }
        PigHouse house = baseMapper.selectById(houseId);
        bucket.set(house);
        bucket.expire(CacheKey.Web.house.duration);
        return house;
    }

    void delCache(Long houseId) {
        redis.getBucket(CacheKey.Web.house.key + houseId).delete();
    }
}
