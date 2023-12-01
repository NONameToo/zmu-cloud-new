package com.zmu.cloud.commons.vo;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SimplePigVo implements Serializable {

    private Long pigId;
    private String earNumber;
    private String individualNumber;
}
