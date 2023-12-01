package com.zmu.cloud.commons.service;


import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.app.IndexMenuTypeQuery;
import com.zmu.cloud.commons.entity.IndexMenuType;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.ZmuApp;
import com.zmu.cloud.commons.vo.IndexMenuFeederDataShowVO;
import com.zmu.cloud.commons.vo.IndexMenuTowerDataShowVO;
import com.zmu.cloud.commons.vo.IndexMenuTypeVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IndexMenuTypeService {

    IndexMenuType getById(Long id);

    Long add(IndexMenuType indexMenuType);

    boolean update(IndexMenuType indexMenuType);

    boolean delete(Long id);

    PageInfo<IndexMenuType> typeList(IndexMenuTypeQuery query);

    List<IndexMenuTypeVO> listCurrent(ZmuApp zmuApp);


    @Transactional
    void saveMyAppMenu(List<Long> menuIds, ZmuApp zmuApp);


    /**
     * APP饲喂器首页展示数据
     * @return
     */
    IndexMenuFeederDataShowVO appIndexMenuFeederDataShow();


    /**
     * APP料塔首页展示数据
     * @return
     */
    IndexMenuTowerDataShowVO appIndexMenuTowerDataShow();
}
