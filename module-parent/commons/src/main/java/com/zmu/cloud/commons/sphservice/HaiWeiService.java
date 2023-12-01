package com.zmu.cloud.commons.sphservice;

import com.zmu.cloud.commons.enums.HaiWeiDevice;

public interface HaiWeiService {

    String find(Long houseId, HaiWeiDevice device);
}
