package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.app.MessageRequestDTO;
import com.zmu.cloud.commons.entity.PushMessage;
import com.zmu.cloud.commons.entity.PushMessageDetail;

public interface PushService {

    void send(PushMessageDetail pmd);

    void sendToAll(PushMessageDetail pmd);

    PushMessage getById(Long id);

    void update(PushMessage pushMessage);

    PageInfo<PushMessage> list(MessageRequestDTO messageRequest);

    boolean read(Long id);

    int unreadCount();

}
