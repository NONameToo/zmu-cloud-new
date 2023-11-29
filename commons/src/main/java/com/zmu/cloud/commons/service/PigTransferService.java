package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.BatchBindDto;
import com.zmu.cloud.commons.dto.TransferPigDTO;
import com.zmu.cloud.commons.dto.TransferPigQuery;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.entity.PigTransfer;
import com.zmu.cloud.commons.entity.PigTransferDetail;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.vo.PigTransferVo;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author YH
 */
public interface PigTransferService {

    @Transactional
    PigTransferVo transferPig(Long houseId, List<TransferPigDTO> pigs);
    /**
     * 转猪：
     *  inHouse、inCol 都为空，则清空栋舍栏位信息
     *  inCol 为空，转到栋舍
     *  inCol 不为空，转到栏位
     * @param pig
     * @param inHouse
     * @param inCol
     */
    void move(PigBreeding pig, Long inHouse, Long inCol);

    PageInfo<PigTransfer> transferPigRecord(BaseQuery query);
    PageInfo<PigTransferDetail> transferPigDetailRecord(TransferPigQuery query);
    List<PigTransferDetail> transferPigDetailRecordForBigCol(Long transferId);

}
