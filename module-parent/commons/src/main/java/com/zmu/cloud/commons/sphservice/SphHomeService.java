package com.zmu.cloud.commons.sphservice;

import com.zmu.cloud.commons.vo.sph.HomeVo;
import com.zmu.cloud.commons.vo.sph.VersionVo;

/**
 * @author YH
 */
public interface SphHomeService {
    HomeVo find(Long farmId);
    void modifyCard(HomeVo vo);
}
