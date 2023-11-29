package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigFarmDTO;
import com.zmu.cloud.commons.dto.PigFarmQuery;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.vo.CompanyAndFarmVo;
import com.zmu.cloud.commons.vo.PigFarmVO;
import com.zmu.cloud.commons.vo.PigTypeVO;
import com.zmu.cloud.commons.vo.UserLoginResultVO;

import java.util.List;
import java.util.Map;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 10:43
 **/
public interface PigFarmService extends IService<PigFarm> {

    Long add(PigFarmDTO pigFarmDTO);

    boolean delete(Long pigFarmId);

    boolean update(PigFarmDTO pigFarmDTO);

    PigFarmVO get(Long pigFarmId, Long userId);

    PageInfo<PigFarmVO> list(PigFarmQuery pigFarmQuery);

    List<PigTypeVO> listPigTypes();

    List<PigFarmVO> listByUserId(Long userId, UserRoleTypeEnum userRoleType);

//    Set<Long> listPigFarmIdsByUserId(Long userId, UserRoleTypeEnum userRoleType);

    void setDefaultPigFarm(Long userId, Long defaultPigFarmId);

    PigFarm findByCache(Long farmId);

    void updateFarm(PigFarm farm);

    List<CompanyAndFarmVo> companyAndFarms(String farmName);
    List<CompanyAndFarmVo> commonUseFarms();
    void cleanCommonUseFarms();

    UserLoginResultVO change(String farmId, String farmName);
}
