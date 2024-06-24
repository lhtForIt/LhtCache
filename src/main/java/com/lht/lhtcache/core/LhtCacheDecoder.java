package com.lht.lhtcache.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Leo
 * @date 2024/06/24
 */
public class LhtCacheDecoder extends ByteToMessageDecoder {

    AtomicLong counter=new AtomicLong();

    //ctx上下文存储信息，in传入的信息，out经过流水线往下传最后输出的结果，我们做的就是把in里面的信息经过处理放到out里面
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("decodeCount:" + counter.incrementAndGet());

        int count = in.readableBytes();
        if (count <=0) return;
        int index = in.readerIndex();
        System.out.println("count:" + count + ",index" + index);

        byte[] bytes = new byte[count];
        in.readBytes(bytes);
        String ret=new String(bytes);
        System.out.println("ret:" + ret);

        out.add(ret);
    }
}
