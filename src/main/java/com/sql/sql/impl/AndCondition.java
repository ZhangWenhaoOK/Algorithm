package com.sql.sql.impl;

import com.sql.sql.Condition;

/**
 * and 条件处理
 */
public class AndCondition implements Condition {

    private final Condition before;
    private final Condition after;

    /**
     * and操作符
     * @param before and之前的条件
     * @param after  and之后的条件
     */
    public AndCondition(Condition before, Condition after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public Boolean apply() {
        // 有一个计算为null，说明sql语句有误，直接返回
        if (before != null && after != null) {
            Boolean beResult = before.apply();
            Boolean afResult = after.apply();
            if (beResult==null || afResult==null) {
                return null;
            }
            // and 操作
            return beResult && afResult;
        }
        return null;
    }
}
