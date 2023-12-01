package com.zmu.cloud.commons.config;

import com.zmu.cloud.commons.mapper.TableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 17:32
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class TableFilterConfig {

    private final TableMapper tableMapper;

    @PostConstruct
    public void init() {
        // 把需要忽略的表放入集合
        List<String> tables = tableMapper.listTables();
        tables.forEach(table -> {
            boolean companyIdColumn = tableMapper.listColumns(table.toLowerCase()).stream()
                    .noneMatch(column -> column.equalsIgnoreCase(MyBatisPlusConfig.COMPANY_ID));
            boolean pigFarmIdColumn = tableMapper.listColumns(table.toLowerCase()).stream()
                    .noneMatch(column -> column.equalsIgnoreCase(MyBatisPlusConfig.PIG_FARM_ID));
            if (companyIdColumn) {
                MyBatisPlusConfig.IGNORE_COMPANY_ID_TABLES.add(table);
            }
            if (pigFarmIdColumn) {
                MyBatisPlusConfig.IGNORE_PIG_FARM_ID_TABLES.add(table);
            }
        });
        log.info("初始化TenantFilter忽略的表：company_id size={}，pig_farm_id size={}",
                MyBatisPlusConfig.IGNORE_COMPANY_ID_TABLES.size(), MyBatisPlusConfig.IGNORE_PIG_FARM_ID_TABLES.size());
    }
}
