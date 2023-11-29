package com.zmu.cloud.demos.producer.entity.param;

import com.zmu.cloud.common.web.entity.param.BaseParam;
import com.zmu.cloud.demos.producer.entity.po.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductQueryParam extends BaseParam<Product> {
    private String name;
}
