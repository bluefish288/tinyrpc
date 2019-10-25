package com.tinyrpc.transport.server.netty;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.transport.server.AbstractServer;
import com.tinyrpc.transport.server.ServerConfig;
import com.tinyrpc.transport.server.netty.handler.PreHandler;
import com.tinyrpc.transport.server.netty.handler.ServerDecoder;
import com.tinyrpc.transport.server.netty.handler.ServerEncoder;
import com.tinyrpc.transport.server.netty.handler.ServerRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer extends AbstractServer {

    private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final static int DEFAULT_WORK_THREAD_COUNT =  Runtime.getRuntime().availableProcessors() * 2;

    private Channel serverChannel;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup(DEFAULT_WORK_THREAD_COUNT);

    private ServerConfig serverConfig;

    @Override
    public void start(ServerConfig serverConfig) throws RpcException {

        this.serverConfig = serverConfig;

        logger.info("server start at " + this.serverConfig.getPort());

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new PreHandler());
                        pipeline.addLast(new ServerDecoder());
                        pipeline.addLast(new ServerEncoder());
                        pipeline.addLast(new ServerRequestHandler(getServiceProcessor()));
                    }
                });

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        ChannelFuture f = b.bind(this.serverConfig.getPort());

        try {
            f = f.sync();
        } catch (InterruptedException e) {
            throw new RpcException(e.getMessage(),e, RpcException.UNKNOWN_EXCEPTION);
        }

        serverChannel = f.channel();

    }

    @Override
    public void close() {
        if (null != serverChannel) {
            serverChannel.close();
            logger.info("close server "+this.serverConfig.getPort());
        }

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        workerGroup = null;
        bossGroup = null;
    }
}