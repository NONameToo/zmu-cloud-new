package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PigHouseRows;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PigHouseRowsMapper extends BaseMapper<PigHouseRows> {

    int countPigNumberByRowsIdAndColumnsId(@Param("rowsId") Long rowsId, @Param("columnsId") Long columnsId);

    @InterceptorIgnore(tenantLine = "true")
    List<PigHouseRows> listByHouseId(@Param("houseId") Long houseId, @Param("companyId") Long companyId, @Param("pigFarmId") Long pigFarmId);

    @InterceptorIgnore(tenantLine = "true")
    List<PigHouseRows> listForPorkLeave(@Param("houseId") Long houseId, @Param("companyId") Long companyId, @Param("pigFarmId") Long pigFarmId);

    PigHouseRows getById(Long id);

    List<PigHouseRows> listTree(@Param("houseId") Long houseId);
}