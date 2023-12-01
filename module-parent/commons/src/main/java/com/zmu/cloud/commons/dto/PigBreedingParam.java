package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.entity.PigBreeding;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@ApiModel("种猪")
public class PigBreedingParam extends PigBreeding {

}
