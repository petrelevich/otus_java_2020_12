package ru.otus.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);
    private static final int COUNTER_LIMIT = 10;
    private int counter = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hi, server!", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws InterruptedException {
        logger.info("Client received: {}", in.toString(CharsetUtil.UTF_8));
        if (counter <= COUNTER_LIMIT) {
            sleep();
            logger.info("counter: {}", counter);
            ctx.writeAndFlush(Unpooled.copiedBuffer("data from client:" + counter, CharsetUtil.UTF_8)).sync();
            counter++;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }

    private void sleep() {
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
