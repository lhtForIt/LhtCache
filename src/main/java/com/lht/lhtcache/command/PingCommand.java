package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/25
 */
public class PingCommand implements Command {
    @Override
    public String name() {
        return "PING";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        String ret = "PONG";
        if (args.length >= 5) {
            ret = args[4];
        }
        return Reply.string(ret);
    }
}
