package com.zmu.cloud.commons.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Supplies;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 疫苗/药品库存Mapper接口
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
public interface SuppliesMapper extends BaseMapper<Supplies>
{
    /**
     * 查询疫苗/药品库存
     * 
     * @param id 疫苗/药品库存主键
     * @return 疫苗/药品库存
     */
    public Supplies selectSuppliesById(Long id);

    /**
     * 查询疫苗/药品库存列表
     * 
     * @param supplies 疫苗/药品库存
     * @return 疫苗/药品库存集合
     */
    public List<Supplies> selectSuppliesList(Supplies supplies);

    /**
     * 新增疫苗/药品库存
     * 
     * @param supplies 疫苗/药品库存
     * @return 结果
     */
    public int insertSupplies(Supplies supplies);

    /**
     * 修改疫苗/药品库存
     * 
     * @param supplies 疫苗/药品库存
     * @return 结果
     */
    public int updateSupplies(Supplies supplies);

    /**
     * 删除疫苗/药品库存
     * 
     * @param id 疫苗/药品库存主键
     * @return 结果
     */
    public int deleteSuppliesById(Long id);

    /**
     * 批量删除疫苗/药品库存
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSuppliesByIds(@Param("ids") Long[] ids,@Param("userId") Long userId);

    Supplies selectSuppliesByNameAndVender(@Param("name") String name,@Param("vender") String vender);

    List<Supplies> listed(Supplies supplies);
}
