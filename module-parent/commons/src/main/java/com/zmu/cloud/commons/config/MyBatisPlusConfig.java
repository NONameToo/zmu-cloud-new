package com.zmu.cloud.commons.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/17 22:13
 **/
@Configuration
public class MyBatisPlusConfig {

    public static final String COMPANY_ID = "company_id";

    public static final String PIG_FARM_ID = "pig_farm_id";

    public static final Set<String> IGNORE_COMPANY_ID_TABLES = new HashSet<>();

    public static final Set<String> IGNORE_PIG_FARM_ID_TABLES = new HashSet<>();

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //先设置 pig_farm_id
        interceptor.addInnerInterceptor(new CustomTenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public String getTenantIdColumn() {
                return PIG_FARM_ID;
            }

            @Override
            public Expression getTenantId() {
                return new LongValue(RequestContextUtils.getRequestInfo().getPigFarmId());
            }

            @Override
            public boolean ignoreTable(String tableName) {
                RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
                // 如果是超级管理员就不过滤
                if (requestInfo.getUserRoleType() == UserRoleTypeEnum.SUPER_ADMIN ) {
                    return true;
                }
                // 猪场id为null
                // 按照目前的设计逻辑 只有两种情况 (LoginInterceptor中已经保证只要请求进来必然是有pigFarmId的)：
                // 一、当前为超级管理员请求
                // 二、当前为非web请求，比如 定时任务之类的，
                // 情况一已经在上一步返回了true，所以此时属于情况二，
                // 这种情况和下面的companyId为空一样，暂时使用mapper查询时手动增加pigFarmId
                if (requestInfo.getPigFarmId() == null) {
                    return true;
                }
                return IGNORE_PIG_FARM_ID_TABLES.contains(tableName);
            }
        }));

        //再设置 company_id
        interceptor.addInnerInterceptor(new CustomTenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public String getTenantIdColumn() {
                return COMPANY_ID;
            }

            @Override
            public Expression getTenantId() {
                return new LongValue(RequestContextUtils.getRequestInfo().getCompanyId());
            }

            @Override
            public boolean ignoreTable(String tableName) {
                RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
                // 如果是超级管理员就不过滤
                if (requestInfo.getUserRoleType() == UserRoleTypeEnum.SUPER_ADMIN) {
                    return true;
                }
                // 公司id为null，说明不是外部请求进来的，属于定时任务之类的
                //  这种情况需要手动在sql中处理多租户的情况 或者  在调用之前手动设置 companyId 到 requestInfo
                if (requestInfo.getCompanyId() == null) {
                    return true;
                }
                return IGNORE_COMPANY_ID_TABLES.contains(tableName);
            }
        }));
        return interceptor;
    }
}
