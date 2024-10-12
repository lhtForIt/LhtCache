package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/29
 */
public class HgetCommand implements Command {

    public static final String OK = "OK";
    @Override
    public String name() {
        return "HGET";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String key = getKey(args);
        String val = getValue(args);
        return Reply.string(cache.hget(key, val));
    }

}
