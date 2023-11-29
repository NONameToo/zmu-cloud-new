package com.zmu.cloud.commons.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Table;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 18:03
 **/
@Slf4j
public class CustomTenantLineInnerInterceptor extends TenantLineInnerInterceptor {

    public CustomTenantLineInnerInterceptor(TenantLineHandler tenantLineHandler) {
        super(tenantLineHandler);
    }

    @Override
    protected Expression builderExpression(Expression currentExpression, List<Table> tables) {
        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(tables)) {
            return currentExpression;
        }
        // 构造每张表的条件
        List<EqualsTo> equalsTos = tables.stream()
                .filter(x -> !getTenantLineHandler().ignoreTable(x.getName()))
                .map(item -> new EqualsTo(getAliasColumn(item), getTenantLineHandler().getTenantId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(equalsTos)) {
            return currentExpression;
        }

        // 注入的表达式
        Expression injectExpression = equalsTos.get(0);
        // 如果有多表，则用 and 连接
        if (equalsTos.size() > 1) {
            for (int i = 1; i < equalsTos.size(); i++) {
                injectExpression = new AndExpression(injectExpression, equalsTos.get(i));
            }
        }

        if (currentExpression == null) {
            return injectExpression;
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), injectExpression);
        } else {
            return new AndExpression(currentExpression, injectExpression);
        }
    }
}
