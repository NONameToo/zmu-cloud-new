package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PigHouseColumns;import com.zmu.cloud.commons.vo.ViewColumnVo;import org.apache.ibatis.annotations.Param;import java.util.List;

public interface PigHouseColumnsMapper extends BaseMapper<PigHouseColumns> {
    List<ViewColumnVo> ViewColumnVo(@Param("farmId") Long farmId, @Param("row") String row);
    List<ViewColumnVo> viewAllCols(@Param("houseId") Long houseId);

    @InterceptorIgnore(tenantLine = "true")
    List<PigHouseColumns> listByRowsId(@Param("rowsId") Long rowsId, @Param("companyId") Long companyId, @Param("pigFarmId") Long pigFarmId);

    void unBind(@Param("pigId") Long pigId);

    void batchUnbind(@Param("pigIds") List<Long> pigIds);

    List<String> viewHouseRows(@Param("houseId") Long houseId);
}