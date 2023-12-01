package com.zmu.cloud.commons.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.config.AliYunPushConfig;
import com.zmu.cloud.commons.dto.app.MessageRequestDTO;
import com.zmu.cloud.commons.entity.PushMessage;
import com.zmu.cloud.commons.entity.PushMessageDetail;
import com.zmu.cloud.commons.entity.PushMessageExtra;
import com.zmu.cloud.commons.enums.AliyunPushEnum;
import com.zmu.cloud.commons.mapper.PushMessageMapper;
import com.zmu.cloud.commons.service.PushService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushServiceImpl extends ServiceImpl<PushMessageMapper, PushMessage> implements PushService {

    final PushMessageMapper pushMessageMapper;
    final AliYunPushConfig pushConfig;
    final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void send(PushMessageDetail pmd) {
        Long userId = RequestContextUtils.getUserId();
        if (CollectionUtils.isEmpty(pmd.getIds())) {
            throw new RuntimeException("用户id列表不可为空");
        }
        List<Long> ids = pmd.getIds();//new ArrayList<>(new HashSet<>(pmd.getAddresses()));
        String title = pmd.getTitle();
        AliyunPushEnum pushEnum = pmd.getPushEnum();
        taskExecutor.execute(() -> {
            List<PushMessage> collect = ids.stream()
                    .map(id -> PushMessage.builder()
                            .userId(id)
                            .createBy(userId)
                            .title(title)
                            .status(0)
                            .createTime(new Date())
                            .pushType(pushEnum.name())
                            .body(pmd.getContent())
                            .extParameters(JSON.toJSONString(pmd.getData(), SerializerFeature.WriteMapNullValue))
                            .type(pmd.getType().name())
                            .subType(pmd.getSubType())
                            .build()
                    ).collect(Collectors.toList());
            if (!Boolean.FALSE.equals(pmd.getDb())) {
                saveBatch(collect);
            }
            if (CollectionUtils.isEmpty(ids)) {
                return;
            }
            pushConfig.push(StringUtils.isBlank(pmd.getPushTitle()) ? title : pmd.getPushTitle(),
                    pmd.getContent(),
                    JSON.toJSONString(PushMessageExtra.builder().subType(pmd.getSubType()).type(pmd.getType()).extras(pmd.getData()).build(), SerializerFeature.WriteMapNullValue),
                    ids,
                    pushEnum);
        });
    }

    @Override
    public void sendToAll(PushMessageDetail pmd) {
    }

    @Override
    public PushMessage getById(Long id) {
        return pushMessageMapper.selectById(id);
    }

    @Override
    public void update(PushMessage pushMessage) {
        pushMessage.setUpdateBy(RequestContextUtils.getUserId());
        pushMessageMapper.updateById(pushMessage);
    }

    @Override
    public PageInfo<PushMessage> list(MessageRequestDTO messageRequest) {
        PageHelper.startPage(messageRequest.getPage(), messageRequest.getSize());
        LambdaQueryWrapper<PushMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushMessage::getUserId, RequestContextUtils.getUserId());
        if (StringUtils.isNotBlank(messageRequest.getType())) {
            wrapper.eq(PushMessage::getType, messageRequest.getType());
        }
        if (messageRequest.getStatus() != null) {
            wrapper.eq(PushMessage::getStatus, messageRequest.getStatus());
        }
        wrapper.orderByDesc(PushMessage::getCreateTime);
        return PageInfo.of(pushMessageMapper.selectList(wrapper));
    }

    @Override
    public boolean read(Long id) {
        return pushMessageMapper.updateStatus(RequestContextUtils.getUserId(), id, new Date()) > 0;
    }

    @Override
    public int unreadCount() {
        return pushMessageMapper.unreadCount(RequestContextUtils.getUserId());
    }
}
