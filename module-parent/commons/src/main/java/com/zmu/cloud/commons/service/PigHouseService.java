package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigHouseDTO;
import com.zmu.cloud.commons.dto.PigHouseQuery;
import com.zmu.cloud.commons.entity.MaterialLine;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.vo.PigHouseTypeVo;
import com.zmu.cloud.commons.vo.PigHouseVO;

import java.util.List;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 21:51
 **/
public interface PigHouseService extends IService<PigHouse> {

    Long add(PigHouseDTO pigHouseDTO);

    boolean update(PigHouseDTO pigHouseDTO);

    boolean delete(Long id);

    PigHouse get(Long id, boolean tree);

    PageInfo<PigHouse> list(PigHouseQuery pigHouseQuery);

    List<PigHouse> listForPorkLeave();

    PigHouse findByCache(Long houseId);

    List<PigHouseTypeVo> houseTypes();
}
