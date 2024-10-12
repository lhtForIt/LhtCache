package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/29
 */
public class HgetallCommand implements Command {

    public static final String OK = "OK";
    @Override
    public String name() {
        return "HGETALL";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String key = getKey(args);
        return Reply.array(cache.hgetall(key));
    }

}
