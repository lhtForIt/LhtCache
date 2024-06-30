package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/30
 */
public class LlenCommand implements Command {
    @Override
    public String name() {
        return "LLEN";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String key = getKey(args);
        return Reply.integer(cache.llen(key));
    }

}
