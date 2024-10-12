package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

import java.net.SocketTimeoutException;
import java.util.stream.Stream;

/**
 * @author Leo
 * @date 2024/06/30
 */
public class ZaddCommand implements Command {
    @Override
    public String name() {
        return "ZADD";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String key = getKey(args);
        String[] scores = getHKeys(args);
        String[] vals = getHValues(args);
        return Reply.integer(cache.zadd(key, vals,toDouble(scores)));
    }

    double[] toDouble(String[] scores) {
        return Stream.of(scores).mapToDouble(Double::parseDouble).toArray();
    }

}
