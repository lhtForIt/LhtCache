package com.lht.lhtcache.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Leo
 * @date 2024/06/21
 */
public class LhtCacheHandler extends SimpleChannelInboundHandler<String> {

    public static final String CRLF = "\r\n";
    public static final String STR_PREFIX = "+";
    public static final String BULK_PREFIX = "$";
    public static final String OK = "OK";
    public static final String INFO = "LhtCache server[v1.0.0],create by Leo." + CRLF;
    public static final LhtCache CACHE = new LhtCache();
    public static final String NULL = "$-1";
    public static final String EMPTY = "$0";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] args = msg.split(CRLF);
        System.out.println("LhtCacheHandler => " + String.join(",", args));
        String cmd=args[2].toUpperCase();

        if ("COMMAND".equals(cmd)) {
            writeByteBuf(ctx, "*2"
                    + CRLF + "$7"
                    + CRLF + "COMMAND"
                    + CRLF + "$4"
                    + CRLF + "PING"
                    + CRLF);
        } else if ("PING".equals(cmd)) {
            String ret = "PONG";
            if (args.length >= 5) {
                ret = args[4];
            }
            simpleString(ctx, ret);
        } else if ("INFO".equals(cmd)) {
            bulkString(ctx,  INFO);
        } else if ("SET".equals(cmd)) {
            CACHE.set(args[4], args[6]);
            simpleString(ctx, OK);
        } else if ("GET".equals(cmd)) {
            String val = CACHE.get(args[4]);
            bulkString(ctx, val);
        } else if ("STRLEN".equals(cmd)) {
            String val = CACHE.get(args[4]);
            integer(ctx, val == null ? 0 : val.length());
        } else if ("DEL".equals(cmd)) {
            int len = (args.length - 3) / 2;
            String[] keys = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 2];
            }
            int del = CACHE.del(keys);
            integer(ctx, del);
        } else if ("EXISTS".equals(cmd)) {
            int len = (args.length - 3) / 2;
            String[] keys = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 2];
            }
            int exists = CACHE.exists(args[4]);
            integer(ctx, exists);
        } else if ("MGET".equals(cmd)) {
            int len = (args.length - 3) / 2;
            String[] keys = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 2];
            }
            array(ctx,CACHE.mGet(keys));
        } else if ("MSET".equals(cmd)) {
            int len = (args.length - 3) / 4;
            String[] keys = new String[len];
            String[] values = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i]=args[4 + i * 4];
                values[i]=args[6 + i * 4];
            }
            CACHE.mSet(keys,values);
            simpleString(ctx, OK);
        } else if ("INCR".equals(cmd)) {
            String key = args[4];
            try {
                integer(ctx, CACHE.incr(key));
            } catch (NumberFormatException nfe) {
                error(ctx, "NFE" + key + " value["+CACHE.get(key)+"] is not an integer.");
            }
        } else if ("DECR".equals(cmd)) {
            String key = args[4];
            try {
                integer(ctx, CACHE.decr(key));
            } catch (NumberFormatException nfe) {
                error(ctx, "NFE" + key + " value["+CACHE.get(key)+"] is not an integer.");
            }
        } else {
            simpleString(ctx, OK);
        }

    }

    private void array(ChannelHandlerContext ctx, String[] array) {
        writeByteBuf(ctx, arrayEncode(array));
    }

    private void error(ChannelHandlerContext ctx, String msg) {
        writeByteBuf(ctx, errorEncode(msg));
    }

    private String errorEncode(String msg) {
        return "-" + msg + CRLF;
    }

    private static String arrayEncode(Object[] array) {
        StringBuilder sb = new StringBuilder();
        if (array == null) {
            sb.append("*-1" + CRLF);
        } else if (array.length == 0) {
            sb.append("*0" + CRLF);
        } else {
            sb.append("*" + array.length + CRLF);
            for (int i = 0; i < array.length; i++) {
                Object obj = array[i];
                if (obj == null) {
                    sb.append("$-1" + CRLF);
                } else{
                    if (obj instanceof Integer) {
                        sb.append(integerEncode((Integer) obj));
                    } else if (obj instanceof String) {
                        sb.append(bulkStrEncode((String) obj));
                    } else if (obj instanceof Object[] objs) {
                        sb.append(arrayEncode(objs));
                    }
                }
            }
        }
        return sb.toString();
    }

    private void integer(ChannelHandlerContext ctx, int i) {
        writeByteBuf(ctx, integerEncode(i));
    }

    private static String integerEncode(int i) {
        return ":" + i + CRLF;
    }

    private void simpleString(ChannelHandlerContext ctx, String content) {
        writeByteBuf(ctx, simpleStrEncode(content));
    }

    private static String simpleStrEncode(String content) {
        String ret;
        if (content == null) {
            ret = NULL;
        } else if (content.length() == 0) {
            ret = EMPTY;
        } else {
            ret = STR_PREFIX + content;
        }
        return ret + CRLF;
    }

    private void bulkString(ChannelHandlerContext ctx, String content) {
        writeByteBuf(ctx, bulkStrEncode(content));
    }

    private static String bulkStrEncode(String content) {
        String ret;
        if (content == null) {
            ret = NULL;
        } else if (content.length() == 0) {
            ret = EMPTY;
        } else {
            ret = BULK_PREFIX + content.getBytes().length + CRLF + content;
        }
        return ret + CRLF;
    }

    private void writeByteBuf(ChannelHandlerContext ctx, String content) {
        System.out.println("wrap byte buffer and reply:" + content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        ctx.writeAndFlush(buffer);
    }

}
