package me.robin.spring.cloud.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017-05-03.
 */
public abstract class CusHeartBeatHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CUSTOM_MSG = 3;
    protected String name;
    private Map<Channel, MutablePair<AtomicInteger, AtomicInteger>> countMap = new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        msg.markReaderIndex();
        try {
            if (msg.getByte(4) == PING_MSG) {
                sendPongMsg(ctx);
            } else if (msg.getByte(4) == PONG_MSG) {
                System.out.println(name + " get pong msg from " + ctx.channel().remoteAddress());
            } else {
                handleData(ctx, msg);
            }
        } finally {
            msg.resetReaderIndex();
        }
    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception;

    protected void sendPingMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PING_MSG);
        context.channel().writeAndFlush(buf);
        AtomicInteger count = countMap.get(context.channel()).getRight();
        System.out.println(name + " sent ping msg to " + context.channel().remoteAddress() + ", count: " + count.incrementAndGet());
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PONG_MSG);
        context.channel().writeAndFlush(buf);
        AtomicInteger count = countMap.get(context.channel()).getLeft();
        System.out.println(name + " sent pong msg to " + context.channel().remoteAddress() + ", count: " + count.incrementAndGet());
    }

    private void sendBuf(ChannelHandlerContext context, ByteBuf byteBuf) {
        try {
            context.channel().writeAndFlush(byteBuf);
        } finally {

        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        countMap.put(ctx.channel(), new MutablePair<>(new AtomicInteger(0), new AtomicInteger(0)));
        System.err.println("---" + ctx.channel().remoteAddress() + " is active---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        countMap.remove(ctx.channel());
        System.err.println("---" + ctx.channel().remoteAddress() + " is inactive---");
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE---");
    }
}
