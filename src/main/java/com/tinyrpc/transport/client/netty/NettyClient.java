package com.tinyrpc.transport.client.netty;

import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.AbstractClient;
import com.tinyrpc.transport.client.netty.handlder.ClientDecoder;
import com.tinyrpc.transport.client.netty.handlder.ClientEncoder;
import com.tinyrpc.transport.client.netty.handlder.ClientRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyClient extends AbstractClient {

//    https://issues.jboss.org/browse/NETTY-424

    private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private ChannelFuture channelFuture;

    private Channel channel;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Lock connectLock = new ReentrantLock();


    public NettyClient(String remoteHost, int remotePort) {
        super(remoteHost, remotePort);
    }

    private Channel channel() {
        if (null == channel) {
            while (true) {
                if (channelFuture.channel().isActive()) {
                    channel = channelFuture.channel();
                    break;
                }
            }
        }
        return channel;
    }

    @Override
    public void connect() throws InterruptedException {

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(
                                new IdleStateHandler(1000, 1000, 1000),
//                                    new HeartbeatIdleStateHandler(),
                                new ClientDecoder(),
                                new ClientEncoder(),
                                new ClientRequestHandler(responseHandler, NettyClient.this));
                    }
                });

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        connectLock.lock();
        try {
            // 绑定端口,同步等待成功.
            channelFuture = b.connect(getRemoteHost(), getRemotePort());

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        channelFuture.sync();
                        channel = channelFuture.channel();
                        logger.info("client start");
                    } else {
                        channelFuture.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    logger.info("client retry");
                                    connect();
                                } catch (InterruptedException e) {
                                    logger.error(e.getMessage(),e);
                                }
                            }
                        }, 1, TimeUnit.SECONDS);
                    }
                }
            });
        } finally {
            connectLock.unlock();
        }

    }

    @Override
    public ResponseFuture send(Request request) throws InterruptedException {

        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(request));
        }

        ResponseFuture future = new ResponseFuture();

        responseHandler.put(request, future);

        ChannelFuture cf = channel().writeAndFlush(request);

        return future;

    }

    @Override
    public void close() {
        if (null == channel) {
            return;
        }
        try {
            this.channel.close().sync();
            group.shutdownGracefully();
        } catch (InterruptedException e) {
            this.channel.close();
        }
    }

}