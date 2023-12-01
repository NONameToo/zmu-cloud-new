package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.PushMessageTypeQuery;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PushMessageType;
import com.zmu.cloud.commons.entity.PushUserType;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PushMessageTypeMapper;
import com.zmu.cloud.commons.mapper.PushUserTypeMapper;
import com.zmu.cloud.commons.service.PushMessageTypeService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.PushMessageTypeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class PushMessageTypeServiceImpl extends ServiceImpl<PushMessageTypeMapper, PushMessageType> implements PushMessageTypeService {

    private final PushUserTypeMapper pushUserTypeMapper;

    @Override
    public PushMessageType getById(Long id) {
        return super.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(PushMessageType pushMessageType) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        pushMessageType.setCreateTime(new Date());
        pushMessageType.setCreateBy(requestInfo.getUserId());
        save(pushMessageType);
        return pushMessageType.getId();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(PushMessageType pushMessageType) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        pushMessageType.setUpdateBy(requestInfo.getUserId());
        return baseMapper.updateById(pushMessageType) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        int count = pushUserTypeMapper.countUserByTypeId(id);
        if (count > 0) {
            throw new BaseException("该消息推送类型已分配给 " + count + " 个用户，请先解除关联后再删除");
        }
        PushMessageType pushMessageType = getById(id);
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        pushMessageType.setUpdateBy(requestInfo.getUserId());
        LambdaUpdateWrapper<PushMessageType> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(PushMessageType::getUpdateBy, requestInfo.getUserId())
                .set(PushMessageType::getDel, true)
                .eq(PushMessageType::getId, id);
        return update(lambdaUpdateWrapper) && pushUserTypeMapper.deleteByTypeId(id) > 0;

    }

    @Override
    public PageInfo<PushMessageType> typeList(PushMessageTypeQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(baseMapper.listPushMessageType(query));
    }

    @Override
    public List<PushMessageTypeVO> listByUserId(Long userId) {
        return baseMapper.listPushMessageTypeByUserId(userId);
    }

    @Override
    public void subAndCancelSub(Long typeId, Boolean subIf,Long userId) {
        LambdaQueryWrapper<PushUserType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushUserType::getTypeId,typeId);
        wrapper.eq(PushUserType::getUserId,userId);
        PushUserType pushUserType = pushUserTypeMapper.selectOne(wrapper);
        //如果有订阅且当前操作是进行订阅
        if (ObjectUtils.isEmpty(pushUserType) && subIf){
            PushUserType pushUserTypeNew = PushUserType.builder().typeId(typeId).userId(userId).build();
            pushUserTypeMapper.insert(pushUserTypeNew);
        }else if (ObjectUtils.isNotEmpty(pushUserType) && !subIf){
            //如果有订阅记录并且是取消订阅
            pushUserTypeMapper.delete(wrapper);
        }else{
            throw  new BaseException("无效操作,请检查参数!");
        }
    }
}
