package com.zmu.cloud.commons.utils;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql操作工具类
 *
 */
public class SqlUtil {
    /**
     * 仅支持字母、数字、下划线、空格、逗号（支持多个字段排序）
     */
    private final static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,]+";

    private static DataSource dataSource = SpringUtils.getBean(DataSource.class);

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            return StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    /**
     * 清理关键字
     * @param sqlScript
     * @return
     */
    public static String clearKeyword(String sqlScript){
        Pattern pattern = Pattern.compile("\\$\\{*.*?\\}");
        Matcher matcher = pattern.matcher(sqlScript);
        while (matcher.find()) {
            String substr = matcher.group();
            sqlScript = sqlScript.replace(substr, "''");
        }



        return sqlScript;
    }



    protected static PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
        return pscf.newPreparedStatementCreator(params);
    }

    /**
     * 替换sql关键字
     * @param sql sql脚本
     * @param keywordMap 关键字Map
     * @return
     */
    public static String replaceSqlKeyword(String sql, Map<String, String> keywordMap) {
        Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String substr = matcher.group();
            String sqlExpr = substr.substring(2, substr.length() - 1);
            sql = sql.replace(substr, keywordMap.get(sqlExpr) != null ? keywordMap.get(sqlExpr) : "");

        }
        return sql;
    }
}
