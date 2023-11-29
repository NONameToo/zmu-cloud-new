package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PigType;import com.zmu.cloud.commons.vo.PigTypeVO;import org.apache.ibatis.annotations.Param;import java.util.List;

/**
 * @author YH
 */
@InterceptorIgnore(tenantLine = "true")
public interface PigTypeMapper extends BaseMapper<PigType> {
    /**
     * 公司下面所有猪场的猪种
     *
     * @param companyId
     * @return
     */
    List<PigTypeVO> list(@Param("companyId") Long companyId);
}