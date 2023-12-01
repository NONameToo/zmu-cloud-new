package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.admin.PushMessageTypeQuery;
import com.zmu.cloud.commons.entity.PushMessageType;
import com.zmu.cloud.commons.vo.PushMessageTypeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PushMessageTypeMapper extends BaseMapper<PushMessageType> {

    List<PushMessageType> listPushMessageType(@Param("query") PushMessageTypeQuery query);

    Set<String> getPushMessageTypeNameByUserId(@Param("userId") Long userId);

    List<PushMessageTypeVO> listPushMessageTypeByUserId(Long userId);
}