package com.zmu.cloud.common.web.entity.vo;

import com.zmu.cloud.common.web.entity.po.BasePo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseVo<T extends BasePo> implements Serializable {
    private String id;
}
