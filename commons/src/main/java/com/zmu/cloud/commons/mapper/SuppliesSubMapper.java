package com.zmu.cloud.commons.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.SuppliesSub;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 疫苗/药品出库Mapper接口
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
public interface SuppliesSubMapper extends BaseMapper<SuppliesSub>
{
    /**
     * 查询疫苗/药品出库
     * 
     * @param id 疫苗/药品出库主键
     * @return 疫苗/药品出库
     */
    public SuppliesSub selectSuppliesSubById(Long id);

    /**
     * 查询疫苗/药品出库列表
     * 
     * @param suppliesSub 疫苗/药品出库
     * @return 疫苗/药品出库集合
     */
    public List<SuppliesSub> selectSuppliesSubList(SuppliesSub suppliesSub);

    /**
     * 新增疫苗/药品出库
     * 
     * @param suppliesSub 疫苗/药品出库
     * @return 结果
     */
    public int insertSuppliesSub(SuppliesSub suppliesSub);

    /**
     * 修改疫苗/药品出库
     * 
     * @param suppliesSub 疫苗/药品出库
     * @return 结果
     */
    public int updateSuppliesSub(SuppliesSub suppliesSub);

    /**
     * 删除疫苗/药品出库
     * 
     * @param id 疫苗/药品出库主键
     * @return 结果
     */
    public int deleteSuppliesSubById(Long id);

    /**
     * 批量删除疫苗/药品出库
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSuppliesSubByIds(@Param("ids") Long[] ids,@Param("userId") Long userId);
}
