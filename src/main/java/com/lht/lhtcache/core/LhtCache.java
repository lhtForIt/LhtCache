package com.lht.lhtcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
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

    // ========================== 2. List End =====================


    // ========================== 3. Set =====================

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
        return zcard(key);
    }

    public int sismember(String key, String val) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<String> exist = entry.getValue();
        return exist.contains(val) ? 1 : 0;
    }

    public int srem(String key, String[] vals) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<String> exist = entry.getValue();
        return vals == null ? 0 : (int) Arrays.stream(vals).map(exist::remove).filter(s -> s).count();
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
    // ========================== 3. Set End =====================

    // ========================== 4. Hash =====================
    public int hset(String key,String[] keys, String[] values) {
        if(keys==null||keys.length==0) return 0;
        if(values==null||values.length==0) return 0;
        if (keys.length!= values.length) return 0;
        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if(entry == null) {
            entry = new CacheEntry<>(new HashMap<String,String>());
            this.map.put(key, entry);
        }
        HashMap<String,String> exist = entry.getValue();
        for(int i = 0; i < keys.length; i++) {
            exist.put(keys[i], values[i]);
        }
        return keys.length;
    }

    public String hget(String key, String val) {
        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if (entry == null) return null;
        HashMap<String,String> exist = entry.getValue();
        return exist.get(val);
    }

    public String[] hgetall(String key) {
        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if (entry == null) return null;
        HashMap<String,String> exist = entry.getValue();
        return exist.entrySet().stream()
                .flatMap(e->Stream.of(e.getKey(),e.getValue())).toArray(String[]::new);
    }

    public String[] hmget(String key, String[] vals) {
        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if (entry == null) return null;
        HashMap<String,String> exist = entry.getValue();
        return vals == null ? new String[0] : Stream.of(vals).map(exist::get).toArray(String[]::new);
    }

    public int hlen(String key) {

        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if (entry == null) return 0;
        HashMap<String,String> exist = entry.getValue();
        return exist.size();

    }

    public int hexists(String key, String val) {
        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if (entry == null) return 0;
        HashMap<String,String> exist = entry.getValue();
        return exist.containsKey(val) ? 1 : 0;
    }

    public int hdel(String key, String[] keys) {
        CacheEntry<HashMap<String,String>> entry = (CacheEntry<HashMap<String,String>>) map.get(key);
        if (entry == null) return 0;
        HashMap<String,String> exist = entry.getValue();
        return keys == null ? 0 : (int) Arrays.stream(keys).map(exist::remove).filter(Objects::nonNull).count();
    }

    // ========================== 4. Hash End =====================

    // ========================== 5. ZSet =====================

    public int zadd(String key, String[] vals, double[] scores) {
        CacheEntry<LinkedHashSet<ZSetEntry>> entry = (CacheEntry<LinkedHashSet<ZSetEntry>>) map.get(key);
        if(entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<ZSetEntry> exist = entry.getValue();
        for(int i = 0; i < vals.length; i++) {
            exist.add(new ZSetEntry(scores[i],vals[i]));
        }
        return vals.length;
    }

    //用泛型屏蔽具体类型，这里其实和里面什么类型无关了
    public int zcard(String key) {
        CacheEntry<?> entry = map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<?> exist = (LinkedHashSet<?>) entry.getValue();
        return exist.size();
    }

    public int zcount(String key, double min, double max) {
        CacheEntry<?> entry = map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<ZSetEntry> exist = (LinkedHashSet<ZSetEntry>) entry.getValue();
        return (int) exist.stream().filter(d -> d.getScore() >= min && d.getScore() <= max).count();
    }

    public String zscore(String key, String val) {
        CacheEntry<?> entry = map.get(key);
        if (entry == null) return "0";
        LinkedHashSet<ZSetEntry> exist = (LinkedHashSet<ZSetEntry>) entry.getValue();
        return exist.stream().filter(d -> d.getVal().equals(val)).map(ZSetEntry::getScore).findFirst().orElse(0d) + "";
    }

    public Integer zrank(String key, String val) {
        CacheEntry<?> entry = map.get(key);
        if (entry == null) return null;
        LinkedHashSet<ZSetEntry> exist = (LinkedHashSet<ZSetEntry>) entry.getValue();
        //这种需要排序，时间复杂度O(nlogn)
//        List<String> sortVals = exist.stream().sorted(Comparator.comparingDouble(ZSetEntry::getScore)).map(ZSetEntry::getVal).collect(Collectors.toList());
//        return sortVals.indexOf(val);
        //这种不需要排序，首先找到元素，然后遍历整个集合看比他小的元素有几个，那么它就是排第几的，时间复杂度是O(n)
        double zscore = Double.parseDouble(zscore(key, val));
        if(zscore == 0d) return null;
        return (int) exist.stream().filter(d -> d.getScore() < zscore).count();

    }

    public int zrem(String key, String[] vals) {
        CacheEntry<?> entry = map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<ZSetEntry> exist = (LinkedHashSet<ZSetEntry>) entry.getValue();
        List<String> vals1 = List.of(vals);
        return vals == null ? 0 : (int) exist.stream().filter(d -> vals1.contains(d.getVal())).map(exist::remove).filter(s -> s).count();
    }

    // ========================== 5. ZSet End =====================

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CacheEntry<T> {
        private T value;
    }


    @Data
    @AllArgsConstructor
    public static class ZSetEntry{
        private double score;
        private String val;
    }

}
