package com.zmu.cloud.commons.sphservice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.entity.SphEmploy;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author YH
 */
public interface SphEmployService extends IService<SphEmploy> {

    Map<String, Object> login(String code, String password);
    void register(String code, String password);
    void logout(HttpServletRequest request);
    Map<String, Object> info(HttpServletRequest request);
}
