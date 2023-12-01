package com.zmu.cloud.commons.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 17:15
 **/
@InterceptorIgnore(tenantLine = "true")
public interface TableMapper {

    @Select("select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA = (select database())")
    List<String> listTables();

    @Select("select COLUMN_NAME from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME= #{tableName}")
    List<String> listColumns(@Param("tableName") String tableName);

    @Select("select 1 from information_schema.TABLES where TABLE_SCHEMA = (select database()) and TABLE_NAME= #{tableName}")
    Integer exists(@Param("tableName") String tableName);


}
