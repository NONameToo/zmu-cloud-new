package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.PushMessageTypeQuery;
import com.zmu.cloud.commons.entity.PushMessageType;
import com.zmu.cloud.commons.vo.PushMessageTypeVO;

import java.util.List;

public interface PushMessageTypeService {

    PushMessageType getById(Long id);

    Long add(PushMessageType pushMessageType);

    boolean update(PushMessageType pushMessageType);

    boolean delete(Long id);

    PageInfo<PushMessageType> typeList(PushMessageTypeQuery query);

    List<PushMessageTypeVO> listByUserId(Long userId);


    void subAndCancelSub( Long typeId,Boolean subIf,Long userId);
}
