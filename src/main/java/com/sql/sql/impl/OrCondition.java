package com.sql.sql.impl;

import com.sql.sql.Condition;

/**
 * or 条件处理
 */
public class OrCondition implements Condition {

    private final Condition before;
    private final Condition after;

    public OrCondition(Condition before, Condition after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public Boolean apply() {
        if (before != null && after != null) {
            Boolean beResult = before.apply();
            Boolean afResult = after.apply();
            if (beResult ==null || afResult==null){
                return null;
            }
            return beResult || afResult;
        }
        return null;
    }
}
