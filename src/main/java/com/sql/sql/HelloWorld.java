package com.sql.sql;

public class HelloWorld {
    public static void main(String[] args) {
        String patten = "^[A-Za-z0-9\\u4e00-\\u9fa5]+[A-Za-z0-9\\u4e00-\\u9fa5._-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]{2,6})+$";
        boolean matches = "youfangwen@neowaycom..".matches(patten);
        System.out.println(matches);
    }
}
