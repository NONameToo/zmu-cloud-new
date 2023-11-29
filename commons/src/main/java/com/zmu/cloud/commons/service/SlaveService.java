package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.enums.SlaveModule;

/**
 * @author YH
 */
public interface SlaveService {

    /**
     * 初始化备库表
     * 1、料塔相关
     * 2、饲喂器相关
     */
    void checkTowerTable(String name);
}
