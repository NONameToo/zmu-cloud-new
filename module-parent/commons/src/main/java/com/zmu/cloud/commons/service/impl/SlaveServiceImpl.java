package com.zmu.cloud.commons.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.zmu.cloud.commons.mapper.TableMapper;
import com.zmu.cloud.commons.service.SlaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YH
 */
@Slf4j
@Service
@DS("slave")
@RequiredArgsConstructor
public class SlaveServiceImpl implements SlaveService {

//    final TowerService towerService;
    final TableMapper tableMapper;
    @Qualifier("jxJdbcTemplate")
    final JdbcTemplate jdbcTemplate;

//    @Async
    @Override
    public void checkTowerTable(String name) {
//        List<String> tables = new ArrayList<>();
//        if ("cloud".equals(name)) {
//            cloud();
//        } else {
//            slave();
//        }
//        System.out.println(tables);

        cloud();
        slave();
        log.info("{}", JSONUtil.parseObj(jdbcTemplate.getDataSource()).toString());
    }

    public void cloud() {
        System.out.println(tableMapper.listTables());
    }


    public void slave() {
        System.out.println(tableMapper.listTables());
    }

}
