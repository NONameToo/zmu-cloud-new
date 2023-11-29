package com.zmu.cloud.commons.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.entity.Supplies;
import com.zmu.cloud.commons.entity.SuppliesAdd;

import java.util.List;

/**
 * 疫苗/药品库存Service接口
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
public interface ISuppliesService extends IService<Supplies>
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
     * 批量删除疫苗/药品库存
     * 
     * @param ids 需要删除的疫苗/药品库存主键集合
     * @return 结果
     */
    public int deleteSuppliesByIds(Long[] ids);

    /**
     * 删除疫苗/药品库存信息
     * 
     * @param id 疫苗/药品库存主键
     * @return 结果
     */
    public int deleteSuppliesById(Long id);

    /**
     * 根据名称和厂家查疫苗/药品
     * @param name
     * @param vender
     * @return
     */
    Supplies selectSuppliesByNameAndVender(String name, String vender);

    List<Supplies> listed(Supplies supplies);
}
