package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.SphEmploy;import java.util.List;

public interface SphEmployMapper extends BaseMapper<SphEmploy> {
    void syncBatchInsert(List<SphEmploy> employs);

    void syncBatchUpdate(List<SphEmploy> employs);
}