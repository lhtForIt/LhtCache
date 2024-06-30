package com.lht.lhtcache.command;

import com.lht.lhtcache.core.Command;
import com.lht.lhtcache.core.LhtCache;
import com.lht.lhtcache.core.Reply;

/**
 * @author Leo
 * @date 2024/06/29
 */
public class InfoCommand implements Command {

    public static final String INFO = "LhtCache server[v1.0.1],create by Leo." + CRLF;
    @Override
    public String name() {
        return "INFO";
    }

    @Override
    public Reply<?> exec(LhtCache cache, String[] args) {
        return Reply.bulkString(INFO);
    }
}
