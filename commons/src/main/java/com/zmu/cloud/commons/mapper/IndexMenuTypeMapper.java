package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.app.IndexMenuTypeQuery;
import com.zmu.cloud.commons.entity.IndexMenuType;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.ZmuApp;
import com.zmu.cloud.commons.vo.IndexMenuTypeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author YH
 */
public interface IndexMenuTypeMapper extends BaseMapper<IndexMenuType> {
    List<IndexMenuType> listIndexMenuType(@Param("query") IndexMenuTypeQuery query);

    Set<String> getIndexMenuTypeNameByUserId(@Param("userId") Long userId);

    List<IndexMenuTypeVO> listIndexMenuTypeByUserId(Long userId);

    List<IndexMenuTypeVO> listIndexMenuTypeByUserAndApp(@Param("userId") Long userId , @Param("zmuApp")ZmuApp zmuApp , @Param("resourceType")String resourceType);

    IndexMenuType selectByPrimaryKey(Long id);
}