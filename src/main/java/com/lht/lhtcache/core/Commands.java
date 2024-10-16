package com.lht.lhtcache.core;

import com.lht.lhtcache.command.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Leo
 * @date 2024/06/25
 */
public class Commands {

    private static final Map<String, Command> all = new HashMap<>();

    static {
        initCommand();
    }

    private static void initCommand() {
        // common command
        register(new PingCommand());
        register(new InfoCommand());
        register(new CommandCommand());

        // string
        register(new SetCommand());
        register(new GetCommand());
        register(new StrlenCommand());
        register(new DelCommand());
        register(new ExistsCommand());
        register(new MgetCommand());
        register(new MsetCommand());
        register(new IncrCommand());
        register(new DecrCommand());

        // list
        // Lpush,Rpush,Lpop,Rpop,Llen,Lindex,Lrange
        register(new LpushCommand());
        register(new LpopCommand());
        register(new RpushCommand());
        register(new RpopCommand());
        register(new LlenCommand());
        register(new LindexCommand());
        register(new LrangeCommand());

        // set
        register(new SaddCommand());
        register(new SmembersCommand());
        register(new SismemberCommand());
        register(new SpopCommand());
        register(new SremCommand());
        register(new ScardCommand());

        // hash
        register(new HgetallCommand());
        register(new HdelCommand());
        register(new HexistsCommand());
        register(new HlenCommand());
        register(new HmgetCommand());
        register(new HgetCommand());
        register(new HsetCommand());

        // zset
        register(new ZaddCommand());
        register(new ZcardCommand());
        register(new ZcountCommand());
        register(new ZrankCommand());
        register(new ZremCommand());
        register(new ZscoreCommand());

    }

    public static void register(Command cmd){
        all.put(cmd.name(),cmd);
    }

    public static Command get(String name) {
        return all.get(name);
    }

    public static String[] getCommandNames(){
        return all.keySet().toArray(new String[0]);
    }

}
