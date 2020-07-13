package com.sql.analysis;

import com.sql.sql.Condition;
import com.sql.sql.impl.AndCondition;
import com.sql.sql.impl.CalcCondition;
import com.sql.sql.impl.OrCondition;

import java.util.Arrays;
import java.util.List;

/**
 * 解析sql的类，返回Condition
 */
public class SqlAnalysis {
    private final JsonAnalysis parser;

    /**
     * 解析sql语句，返回解析的结果 Condition
     *
     * @param sql 要解析的sql语句
     * @return sql解析结果
     */
    public Condition analysisSql(String sql) {
        String[] s = sql.trim().split("\\s+");
        List<String> sqls = Arrays.asList(s);
        return getCondition(sqls);
    }

    /**
     * 构造方法，传入json解析对象，方便后面取值
     *
     * @param parser json解析类
     */
    public SqlAnalysis(JsonAnalysis parser) {
        this.parser = parser;
    }

    private Condition getCondition(List<String> sql) {
        // TODO 优先处理括号 ()
        // 遇到or关键字，语句分为两段
        int or = sql.indexOf("or");
        if (or != -1) {
            // 递归调用
            List<String> pre = sql.subList(0, or);
            List<String> post = sql.subList(or + 1, sql.size());
            return new OrCondition(getCondition(pre), getCondition(post));
        }
        // 遇到and关键字，sql语句分为前后两段
        int and = sql.indexOf("and");
        if (and != -1) {
            List<String> pre = sql.subList(0, and);
            List<String> post = sql.subList(and + 1, sql.size());
            return new AndCondition(getCondition(pre), getCondition(post));
        }
        // 没有and或者or关键字的简单语句解析
        return getCalc(sql);
    }


    // 简单语句解析
    private CalcCondition getCalc(List<String> sql) {
        CalcCondition calcCondition = null;
        // 如果是三段，类似 a = 2
        if (sql.size() == 3) {
            String key = sql.get(0);
            String type = sql.get(1);
            String value = sql.get(2);
            // 如果key字段包含运算符，则判断sql语句有误
            if (!(key.contains("<") || key.contains(">") || key.contains("="))) {
                // 封装简单的计算类
                calcCondition = new CalcCondition(type, key, value, parser);
            }
            return calcCondition;
        } else if (sql.size() == 1 || sql.size() == 2) {
            // 类似 a=2 || a= 2 || a =2
            String simple;
            if (sql.size() == 2) {
                simple = sql.get(0) + sql.get(1);
            } else {
                simple = sql.get(0);
            }
            // 如果未包含计算符或者包含多个计算符，则判断sql语句有误
            int count = 0;
            for (int i = 0; i < simple.length(); i++) {
                char c = simple.charAt(i);
                if (c == '<' || c == '=' || c == '>') {
                    count++;
                }
            }
            // 正常语句
            if (count == 1) {
                // 小于的简单计算
                String[] s1 = simple.split("<");
                if (s1.length == 2) {
                    calcCondition = new CalcCondition("<", s1[0], s1[1], parser);
                    return calcCondition;
                }
                // 大于的简单计算
                String[] s2 = simple.split(">");
                if (s2.length == 2) {
                    calcCondition = new CalcCondition(">", s2[0], s2[1], parser);
                    return calcCondition;
                }
                // 等于的简单计算
                String[] s3 = simple.split("=");
                if (s3.length == 2) {
                    calcCondition = new CalcCondition("=", s3[0], s3[1], parser);
                    return calcCondition;
                }
            }
        }
        // sql语句有误，返回一个计算类型不存在的计算类
        calcCondition = new CalcCondition();
        return calcCondition;
    }
}
