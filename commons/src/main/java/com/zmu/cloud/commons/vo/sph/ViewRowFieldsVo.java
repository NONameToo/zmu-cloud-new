package com.zmu.cloud.commons.vo.sph;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewRowFieldsVo {

    //栏位ID
    private Long fieldId;
    //栏位是否有猪
    private boolean enable;
    //耳号
    private String earNumber;

}
