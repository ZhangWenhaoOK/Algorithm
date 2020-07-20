package com.redis;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;

/**
 * 布隆过滤器
 */
public class BronFilter {
    private static final int size = 1000000;//预计要插入多少数据
    private static final double fpp = 0.00001;//期望的误判率
    private static final Funnel<CharSequence> charSequenceFunnel = Funnels.stringFunnel(StandardCharsets.UTF_8);
    private static final BloomFilter<String> bloomFilter = BloomFilter.create(charSequenceFunnel, size, fpp);

    public static void main(String[] args) {

        //插入数据
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i + "");
        }
        int count = 0;
        System.out.println("添加完成");
        for (int i = 1000000; i < 2000000; i++) {
            if (bloomFilter.mightContain(i + "")) {
                count++;
                System.out.println(i + "误判了");
            }
        }
        System.out.println("总共的误判数:" + count);
    }
}
