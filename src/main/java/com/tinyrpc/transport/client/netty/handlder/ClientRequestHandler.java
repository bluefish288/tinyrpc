package com.tinyrpc.transport.client.netty.handlder;

import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.transport.client.ResponseHandler;
import com.tinyrpc.transport.client.netty.NettyClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ClientRequestHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ClientRequestHandler.class);

    private final static int CONNECT_RETRY_SECONDS = 1;

    private ResponseHandler responseHandler;

    private NettyClient client;

    public ClientRequestHandler(ResponseHandler responseHandler, NettyClient client) {
        this.responseHandler = responseHandler;
        this.client = client;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("-------------offline");
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }, CONNECT_RETRY_SECONDS, TimeUnit.SECONDS);

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Response response = (Response) msg;
        if(logger.isDebugEnabled()){
            logger.debug(String.valueOf(response));
        }

        responseHandler.handle(response);

    }
}