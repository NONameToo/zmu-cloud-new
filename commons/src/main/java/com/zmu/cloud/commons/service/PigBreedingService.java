package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.BaseFeedingDTO;
import com.zmu.cloud.commons.dto.PigBreedingImportDto;
import com.zmu.cloud.commons.dto.PigBreedingParam;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigFarmTask;
import com.zmu.cloud.commons.vo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shining
 */
public interface PigBreedingService extends PigService {

    List<PigBreeding> findByCol(Long colId);
    List<PigBreeding> findByHouse(Long houseId);
    /**
     * 种猪列表，并分页
     *
     * @param queryPigBreeding
     * @return
     */
    PageInfo<PigBreedingListVO> page(QueryPig queryPigBreeding);

    /**
     * 缓存中查找猪只
     * @param earNumber
     * @return
     */
    List<SimplePigVo> findByCache(String earNumber);

    /**
     * 添加种猪
     */
    void add(PigBreedingParam pigBreeding);

    /**
     * 编辑种猪
     *
     * @param pigBreeding
     */
    void update(PigBreedingParam pigBreeding);

    void delete(Long id);

    /**
     * 种猪导入
     * @param pigHouseId
     * @param file
     * @return
     * @throws IOException
     */
    List<PigBreedingImportDto> importPig(String pigHouseId, MultipartFile file) throws IOException;

    /**
     * 种猪导入校验
     * @param file
     * @return
     * @throws IOException
     */
    Set<PigBreedingImportDto> importCheck(MultipartFile[] file) throws IOException;

    /**
     * 种猪详情
     *
     * @param id
     * @return
     */
    PigBreedingListVO detail(Long id);

    /**
     * @param id 种猪数据统计
     */
    PigBreedingStatisticsVO statistics(Long id);

    /**
     * 事件明细
     */
    List<EventPigBreedingVO> eventDetail(Long id);

    /**
     * 采精事件明细
     *
     * @param id
     * @return
     */
    List<EventSemenCollectionVO> eventSemenDetail(Long id);

    List<EventBoarDetailVO> eventSemenAppDetail(Long id);

    /**
     * 种猪事件记录查询
     */
    PageInfo<EventPigBreedingListVO> event(QueryPig queryPigBreeding);

    PageInfo<PigBreedingListWebVO> selectByEarNumber(QueryPig queryPig);

    PageInfo<PigBreedingArchivesListVO> pageArchives(QueryPig queryPig);


    List<PigBreedingBoarListVO> boarList();

    List<PigBreedingListWebVO> selectListByEarNumber(QueryPig queryPig);

    List<EventPrintPigBreedingVO> eventPrintDetail(Long id);

    /**
     * 超过一年未操作猪只
     * @return
     */
    List<PigBreedingLoseVo> pigBreedingLoseList();
}
