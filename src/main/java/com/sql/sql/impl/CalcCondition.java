package com.sql.sql.impl;

import com.sql.analysis.JsonAnalysis;
import com.sql.sql.Condition;

/**
 * 计算条件处理
 * < = >
 */
public class CalcCondition implements Condition {
    // < = > like
    private Type type;
    private String key;
    // value为原始数值，在判断的时候再进行计算(能晚一点干的事，不要提前干)
    private String value;
    // json解析对象，用key获取值
    private JsonAnalysis parser;
    // 是否已经计算过，计算一次即可，无需多次计算
    private boolean isCalc;
    // 计算的结果
    private Boolean calcResult;

    public CalcCondition() {
    }

    public CalcCondition(String type, String key, String value, JsonAnalysis parser) {
        switch (type) {
            case ">":
                this.type = Type.greater;
                break;
            case "<":
                this.type = Type.less;
                break;
            case "=":
                this.type = Type.equal;
                break;
            case "like":
                this.type = Type.like;
                break;
            default:
                break;
        }
        this.key = key;
        this.value = value;
        this.parser = parser;
    }

    @Override
    public Boolean apply() {
        if (isCalc) {
            // 如果已经计算过
            return calcResult;
        }
        // 不存在的计算类型
        if (type == null) {
            isCalc = true;
            return calcResult;
        }
        // key恒为String  value "123"  123
        key = parser.getValue(key);
        boolean isStr = false;
        if (key == null || value == null) {
            isCalc = true;
            return calcResult;
        }
        if ('\"' == value.charAt(0) && '\"' == value.charAt(value.length() - 1)) {
            isStr = true;
            value = value.substring(1, value.length() - 1);
        }
        switch (type) {
            case less:
                try {
                    // value 被双引号包裹
                    if (isStr) {
                        calcResult = key.compareTo(value) < 0;
                        isCalc = true;
                        return calcResult;
                    }
                    // 按数值计算
                    double key = Double.parseDouble(this.key);
                    double value = Double.parseDouble(this.value);
                    calcResult = key < value;
                    isCalc = true;
                    return calcResult;
                } catch (Exception e) {
                    isCalc = true;
                    return calcResult;
                }
            case greater:
                try {
                    // value 被双引号包裹
                    if (isStr) {
                        calcResult = key.compareTo(value) > 0;
                        isCalc = true;
                        return calcResult;
                    }
                    double key = Double.parseDouble(this.key);
                    double value = Double.parseDouble(this.value);
                    calcResult = key > value;
                    isCalc = true;
                    return calcResult;
                } catch (Exception e) {
                    isCalc = true;
                    return calcResult;
                }
            case equal:
                try {
                    // value 被双引号包裹
                    if (isStr) {
                        calcResult = key.compareTo(value) == 0;
                        isCalc = true;
                        return calcResult;
                    }
                    double key = Double.parseDouble(this.key);
                    double value = Double.parseDouble(this.value);
                    calcResult = key == value;
                    isCalc = true;
                    return calcResult;
                } catch (Exception e) {
                    isCalc = true;
                    return calcResult;
                }
            case like:
                if (isStr) {
                    value = value.replaceAll("%", ".*");
                    calcResult = key.matches(value);
                    isCalc = true;
                    return calcResult;
                }
                calcResult = false;
                isCalc = true;
                return false;
            default:
                isCalc = true;
                return calcResult;
        }

    }

    public enum Type {
        less,
        equal,
        greater,
        like
    }
}
