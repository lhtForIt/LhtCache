package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/30
 */
public class MsetCommand implements Command {

    public static final String OK = "OK";

    @Override
    public String name() {
        return "MSET";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String[] keys = getKeys(args);
        String[] values = getValues(args);
        cache.mSet(keys, values);
        return Reply.string(OK);
    }

}
