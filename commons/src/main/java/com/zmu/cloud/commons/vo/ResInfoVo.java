package com.zmu.cloud.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhaojian
 * @create 2023/10/17 10:52
 * @Description 巨星返回数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResInfoVo {

    private long resid;
    private long upresid;

}
