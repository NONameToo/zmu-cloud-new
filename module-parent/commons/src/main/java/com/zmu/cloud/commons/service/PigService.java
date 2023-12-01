package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.BatchBindDto;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.vo.SimplePigVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 智慧猪家接口
 * @author YH
 */
public interface PigService {

    /**
     * 选择猪只，可通过个体号、二号搜索
     * @return
     */
    List<Pig> chosePig(String keyword, Integer type);
    Set<SimplePigVo> search(ResourceType source, String keyword);
    Optional<Pig> findPig(Long id);

    /**
     * 猪只绑定到栏位
     * @param colId
     * @param pigId
     */
    @Transactional
    void bindToField(ResourceType source, Long colId, Long pigId);
    /**
     * 猪只移出栏位
     * @param pigId
     */
    void moveOutForField(ResourceType source, Long pigId);
    void moveOutForField(ResourceType source, Long colId, Long pigId);
    @Transactional
    void batchBind(ResourceType source, List<BatchBindDto> batchBindDtos);


}
