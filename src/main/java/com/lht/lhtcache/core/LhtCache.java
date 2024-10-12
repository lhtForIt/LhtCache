package com.lht.lhtcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 * cache entries.
 *
 * @author Leo
 * @date 2024/06/24
 */
public class LhtCache {

    Map<String, CacheEntry<?>> map = new HashMap<>();

    // ========================== 1. String =====================

    public String get(String key) {
        CacheEntry<String> cacheEntry = (CacheEntry<String>) map.get(key);
        return cacheEntry == null ? null : cacheEntry.getValue();
    }

    public void set(String key, String value) {
        map.put(key, new CacheEntry<>(value));
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
        return keys == null ? new String[0] : Stream.of(keys).map(this::get).toArray(String[]::new);
    }


    public void mSet(String[] keys, String[] values) {
        if (keys==null||keys.length==0) return;
        for (int i = 0; i < keys.length; i++) {
            set(keys[i], values[i]);
        }
    }

    //要考虑int溢出和非数字类型(string)的情况
    public int incr(String key) {
        String val = get(key);
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
        CacheEntry<String> cacheEntry = (CacheEntry<String>) map.get(key);
        String val = cacheEntry.value;
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

    public int strlen(String key) {
        return get(key) == null ? 0 : get(key).length();
    }

    // ========================== 1. String  End =====================


    // ========================== 2. List =====================
    public int lpush(String key, String[] vals) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if(entry == null) {
            entry = new CacheEntry<>(new LinkedList<>());
            this.map.put(key, entry);
        }
        LinkedList<String> exist = entry.getValue();
        Arrays.stream(vals).forEach(exist::addFirst);
        return vals.length;
    }

    public String[] lpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if (exist == null || exist.size()==0 ) return null;

        int len = Math.min(count, exist.size());
        String[] ret= new String[len];
        int index = 0;
        while (index < len) {
            ret[index++] = exist.removeFirst();
        }
        return ret;
    }

    public int rpush(String key, String[] vals) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedList<>());
            this.map.put(key, entry);
        }
        LinkedList<String> exist = entry.getValue();
//        Arrays.stream(vals).forEach(exist::addFirst);
        exist.addAll(Arrays.asList(vals));
        return vals.length;

    }

    public String[] rpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if (exist == null || exist.size()==0 ) return null;

        int len = Math.min(count, exist.size());
        String[] ret= new String[len];
        int index = 0;
        while (index < len) {
            ret[index++] = exist.removeLast();
        }
        return ret;
    }

    public int llen(String key) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedList<String> exist = entry.getValue();
        if (exist == null) return 0;
        return exist.size();
    }

    public String lindex(String key, int index) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if (exist == null) return null;
        if(index >= exist.size()) return null;
        return exist.get(index);
    }

    public String[] lrange(String key, int start, int end) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if (exist == null) return null;
        int size = exist.size();
        if (start >= size) return null;
        if(end >= size) end = size - 1;
        int len = Math.min(size, end - start + 1);
        String[] ret = new String[len];

        for (int i = 0; i < len; i++) {
            ret[i] = exist.get(start + i);
        }

        return ret;


    }

    public int sadd(String key, String[] vals) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if(entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<String> exist = entry.getValue();
        exist.addAll(List.of(vals));
        return vals.length;
    }


    public String[] smembers(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if(entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<String> exist = entry.getValue();
        return exist.toArray(new String[0]);
    }

    public int scard(String key) {

        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<String> exist = entry.getValue();
        if (exist == null) return 0;
        return exist.size();

    }

    public int sismember(String key, String val) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<String> exist = entry.getValue();
        if (exist == null) return 0;
        return exist.contains(val) ? 1 : 0;
    }

    public int srem(String key, String[] vals) {


        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<String> exist = entry.getValue();
        if (exist == null) return 0;
        return vals == null ? 0 : (int) Arrays.stream(vals).map(exist::remove).filter(Objects::nonNull).count();
    }

    public String[] spop(String key, int count) {

        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<String> exist = entry.getValue();
        if (exist == null || exist.size()==0 ) return null;

        int len = Math.min(count, exist.size());
        String[] ret= new String[len];
        Random r = new Random();
        int index = 0;
        while (index < len) {
            // 将set转成数组，并随机取一个元素删除
            String obj = exist.toArray(new String[0])[r.nextInt(exist.size())];
            exist.remove(obj);
            ret[index++] = obj;
        }
        return ret;

    }

    // ========================== 2. List End =====================

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CacheEntry<T> {
        private T value;
    }

}
