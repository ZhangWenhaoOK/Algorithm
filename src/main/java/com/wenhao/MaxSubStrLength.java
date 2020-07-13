package com.wenhao;

import java.util.HashMap;
import java.util.Map;

/**
 * 求给定字符串中最长的字符不重复的字符串的长度
 * abcd  4
 * bbbbb 1
 * pwwkew pw 2  wke 3 w 1
 */
public class MaxSubStrLength {
    public static int lengthOfLongestSubstring(String s) {
        int n = s.length(), ans = 0;
        if (n==0){
            return 0;
        }
        Map<Character, Integer> map = new HashMap<>();
        // j 开始循环
        // 时间复杂度O(n)
        for (int j = 0,i = 0; j < n; j++) {
            // 将字符和下标加入map集合中，如果查找到，说明重复
            if (map.containsKey(s.charAt(j))) {
                // i 下标移动到
                i = Math.max(map.get(s.charAt(j)), i);
            }
            ans = Math.max(ans, j - i + 1);
            map.put(s.charAt(j), j + 1);
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstring("abce"));
    }
}
