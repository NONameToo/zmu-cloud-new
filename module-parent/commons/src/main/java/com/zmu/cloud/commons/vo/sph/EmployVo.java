package com.zmu.cloud.commons.vo.sph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployVo {

    private Long id;
    private String employCode;
    private String name;
    private String phone;
    private String icon;
    private String email;
    private String nickName;
    private Long companyId;
    private String companyName;
    private Long farmId;
    private String farmName;
    private String position;

}
