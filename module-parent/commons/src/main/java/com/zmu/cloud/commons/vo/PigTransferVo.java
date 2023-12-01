package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.PigTransfer;
import com.zmu.cloud.commons.entity.PigTransferDetail;
import lombok.Data;

import java.util.List;

/**
 * @author YH
 */
@Data
public class PigTransferVo extends PigTransfer {
    List<PigTransferDetail> details;
}
