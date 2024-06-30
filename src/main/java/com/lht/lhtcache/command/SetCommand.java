package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/29
 */
public class SetCommand implements Command {

    public static final String OK = "OK";
    @Override
    public String name() {
        return "SET";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String key = getKey(args);
        String value = getValue(args);
        cache.set(key, value);
        return Reply.string(OK);
    }

}
