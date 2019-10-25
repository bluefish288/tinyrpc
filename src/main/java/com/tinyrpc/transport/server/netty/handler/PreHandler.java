package com.tinyrpc.transport.server.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class PreHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(PreHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("PreHandler threadId="+Thread.currentThread().getId());

//        logger.info("--------"+msg.getClass());

        SocketAddress socketAddress = ctx.channel().remoteAddress();

//        logger.info(socketAddress.getClass());
        logger.info(String.valueOf(socketAddress));

        if(socketAddress instanceof InetSocketAddress){
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            InetAddress address = inetSocketAddress.getAddress();
            if(null!=address){
                logger.info("address : "+address.getHostAddress());
                logger.info("address : "+address.getHostName());
            }
            logger.info(inetSocketAddress.getHostName());
            logger.info(inetSocketAddress.getHostString());
        }


        if(msg instanceof ByteBuf){
            logger.info(String.valueOf(((ByteBuf)msg).readableBytes()));
        }

        ctx.fireChannelRead(msg);
    }
}