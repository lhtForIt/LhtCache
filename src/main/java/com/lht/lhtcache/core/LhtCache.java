package com.lht.lhtcache.core;

import org.springframework.cache.Cache;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * cache entries.
 *
 * @author Leo
 * @date 2024/06/24
 */
public class LhtCache {

    Map<String, String> map = new HashMap<>();

    public String get(String key) {
        return map.get(key);
    }

    public void set(String key, String value) {
        map.put(key, value);
    }

    public int del(String... keys) {
        return keys == null ? 0 : (int) Arrays.stream(keys)
                .map(map::remove).filter(Objects::nonNull).count();
    }

    public int exists(String... keys) {
        return keys == null ? 0 : (int) Arrays.stream(keys)
                .filter(map::containsKey).count();
//        return keys == null ? 0 : (int) Arrays.stream(keys).
//                map(map::containsKey).filter(x->x).count();
    }

    public String[] mGet(String... keys) {
        return keys == null ? new String[0] : Stream.of(keys).map(map::get).toArray(String[]::new);
    }


    public void mSet(String[] keys, String[] values) {
        if (keys==null||keys.length==0) return;
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
    }

    //要考虑int溢出和非数字类型(string)的情况
    public int incr(String key) {
        String val = map.get(key);
        int count = 0;
        try {
            if (val != null) {
                count = Integer.parseInt(val);
            }
            set(key, String.valueOf(++count));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }
        return count;
    }

    public int decr(String key) {
        String val = map.get(key);
        int count = 0;
        try {
            if (val != null) {
                count = Integer.parseInt(val);
            }
            set(key, String.valueOf(--count));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }
        return count;
    }
}
