package com.zmu.cloud.commons.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Area;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AreaMapper extends BaseMapper<Area> {

    Area selectByPrimaryKey(Integer id);

    List<Area> listAllProvince();

    List<Area> listAllCity(@Param("provinceId") Integer provinceId);

    List<Area> listAllArea(@Param("cityId") Integer cityId);

}