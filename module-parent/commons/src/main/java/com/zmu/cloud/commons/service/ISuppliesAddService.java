package com.zmu.cloud.commons.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.entity.SuppliesAdd;

import java.util.List;

/**
 * 疫苗/药品入库Service接口
 * 
 * @author zhaojian
 * @date 2023-11-07
 */
public interface ISuppliesAddService  extends IService<SuppliesAdd>
{
    /**
     * 查询疫苗/药品入库
     * 
     * @param id 疫苗/药品入库主键
     * @return 疫苗/药品入库
     */
    public SuppliesAdd selectSuppliesAddById(Long id);

    /**
     * 查询疫苗/药品入库列表
     * 
     * @param suppliesAdd 疫苗/药品入库
     * @return 疫苗/药品入库集合
     */
    public List<SuppliesAdd> selectSuppliesAddList(SuppliesAdd suppliesAdd);

    /**
     * 新增疫苗/药品入库
     * 
     * @param suppliesAdd 疫苗/药品入库
     * @return 结果
     */
    public int insertSuppliesAdd(SuppliesAdd suppliesAdd);

    /**
     * 修改疫苗/药品入库
     * 
     * @param suppliesAdd 疫苗/药品入库
     * @return 结果
     */
    public int updateSuppliesAdd(SuppliesAdd suppliesAdd);

    /**
     * 批量删除疫苗/药品入库
     * 
     * @param ids 需要删除的疫苗/药品入库主键集合
     * @return 结果
     */
    public int deleteSuppliesAddByIds(Long[] ids);

    /**
     * 删除疫苗/药品入库信息
     * 
     * @param id 疫苗/药品入库主键
     * @return 结果
     */
    public int deleteSuppliesAddById(Long id);
}