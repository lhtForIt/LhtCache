package com.lht.lhtcache;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage;
import io.netty.handler.codec.redis.BulkStringHeaderRedisMessage;
import io.netty.handler.codec.redis.DefaultBulkStringRedisContent;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author Leo
 * @date 2024/06/21
 */
public class LhtCacheHandler extends SimpleChannelInboundHandler<RedisMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RedisMessage msg) throws Exception {
        if (msg instanceof ArrayHeaderRedisMessage m) {
            System.out.println("1 => "+m.length());
        }

        if (msg instanceof BulkStringHeaderRedisMessage m) {
            System.out.println("2 => "+m.bulkStringLength());
        }

        if (msg instanceof DefaultBulkStringRedisContent m) {
            int count = m.content().readableBytes();
            byte[] bytes=new byte[count];
            m.content().readBytes(bytes);
            System.out.println("3 => " + new String(bytes));
        }
    }
}
