package com.sql;

import com.sql.analysis.JsonAnalysis;
import com.sql.analysis.SqlAnalysis;
import com.sql.sql.Condition;

import java.util.Scanner;

/*
 * {"a":1,"b":{"c":1.23},"d":"hello"}
 *
 * a = "somethine" false
 * a = 2 and d like "he%" or a = 1    true
 * 优 a = 2 or b.c < 3  true
 * 支持  a=2  a= 2  a = 2   字符串必须加双引号  否则为数字或者在json中取值
 *
 * */
public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String sql2 = "a = 2 and d like \"he%\"";
        String sql3 = "a = 2 or b.c < 3";
        String sql4 = "        d like \"he%lo\"";
        String sql5 = "   ";
        Scanner scanner = new Scanner(System.in);
//        String sql = scanner.nextLine();
        String sql = sql4;
        if (sql == null || sql.trim().length() == 0) {
            System.out.println("sql语句为空");
        } else {
            JsonAnalysis instance = JsonAnalysis.getInstance("file.json");
            SqlAnalysis sqlAnalysis = new SqlAnalysis(instance);
            Condition condition = sqlAnalysis.analysisSql(sql);
            Boolean apply = condition.apply();
            if (apply == null) {
                System.out.println("sql语句有误");
            } else {
                System.out.println(apply);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end- start);
    }
}
