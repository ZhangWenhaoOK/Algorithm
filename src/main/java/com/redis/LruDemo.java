package com.redis;

import org.apache.commons.collections.map.LRUMap;

public class LruDemo {
    public static void main(String[] args) {
        LRUMap lruMap = new LRUMap(3);
        lruMap.put(1,1);
        lruMap.put(2,2);
        lruMap.put(3,3);
        lruMap.get(1);
        lruMap.put(4,4);
        lruMap.put(5,5);
        Object o = lruMap.get(1);
        System.out.println(o);

    }
}
