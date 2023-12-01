package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.InitColDto;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.dto.PigHouseColumnsDTO;
import com.zmu.cloud.commons.dto.SaveColumnDto;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import com.zmu.cloud.commons.vo.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PigHouseColumnsService extends IService<PigHouseColumns> {

    /**
     * 暂时只支持一个栏位一个饲喂器
     * @param clientId
     * @param feederCode
     * @return
     */
    Optional<PigHouseColumns> findByFeeder(Long clientId, Integer feederCode);
    Optional<PigHouseColumns> findByQrcode(QrcodeVO vo);
    Optional<PigHouseColumns> findByPig(Long pigId);
    Optional<Pig> findByCol(Long colId);
    List<PigHouseColumns> findByClientId(Long clientId);
    @Transactional
    void batchUnbind(Long colId, String endPosition);
    Optional<PigHouseColumns> findByPositionAndHouseId(Long houseId, String position);
    List<PigHouseColumns> findByPositionAndHouseId(Long houseId, String beginPosition, String endPosition);

    @Transactional
    void save(SaveColumnDto colDto);
    ColumnVo detail(Long colId);

    List<PigHouseColumns> listByHouse(Long houseId);
    List<PigHouseColumns> listByRow(Long rowId);
    @Transactional
    void batchBindFeeder(Long rowId, Long clientId, Integer batch);
    @Transactional
    void init(InitColDto initColDto);

    List<PigHouseColumns> waitBatchBind();
    List<ViewRowVo> viewHouseRows(Long houseId, ColumnOperateType operationType);
    List<ViewColumnVo> viewColumns(String row);
    List<ViewColumnVo> allCols(Long houseId);
    void choseColumns(ViewRowVo viewRowVo);
    void choseFieldsForTransferPig(ViewRowVo viewRowVo);
    List<ViewRowVo> choose(ColumnOperateType operationType, Long houseId);
    ChooseCols chooseForTransferPig(String houseId, ColumnOperateType operationType);
    void choseRemove(ViewRowVo viewRowVo);



    Long add(PigHouseColumnsDTO pigHouseColumnsDTO);
    void update(PigHouseColumnsDTO pigHouseColumnsDTO);
    void delete(Long pigHouseColumnsId);
    List<PigHouseColumns> saveBatch(List<PigHouseColumns> pigHouseColumns);

    void batchChangeAmount(Long fieldId, String endViewCode,Integer value);
}
