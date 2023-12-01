package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;

import com.zmu.cloud.commons.entity.PushMessage;
import org.apache.ibatis.annotations.Param;

public interface PushMessageMapper extends BaseMapper<PushMessage> {

    int updateStatus(@Param("userId") Long userId, @Param("id") Long id, @Param("time") Date time);

    int unreadCount(@Param("userId") Long userId);
}