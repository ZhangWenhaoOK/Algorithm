package com.sql.sql;

/**
 * 封装了各种条件
 */
public interface Condition {
    /**
     * 条件执行结果
     * null 语句有误
     * true 结果真
     * false 结果假
     * @return 条件执行结果
     */
    Boolean apply();
}
