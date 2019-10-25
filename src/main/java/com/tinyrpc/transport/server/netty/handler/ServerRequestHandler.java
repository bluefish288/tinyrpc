package com.tinyrpc.transport.server.netty.handler;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Heartbeat;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.transport.server.netty.ChannelManager;
import com.tinyrpc.transport.server.ServiceProcessor;
import com.tinyrpc.transport.server.netty.filter.AccessLogFilter;
import com.tinyrpc.transport.server.netty.filter.InvokeFilter;
import com.tinyrpc.transport.server.netty.filter.Invoker;
import com.tinyrpc.transport.server.netty.filter.ServiceProcessFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ServerRequestHandler.class);

    private List<InvokeFilter> filters = new ArrayList<>();

    public ServerRequestHandler(ServiceProcessor serviceProcessor) {
        filters.add(new AccessLogFilter());
        filters.add(new ServiceProcessFilter(serviceProcessor));
    }

    private final ChannelManager channelManager = new ChannelManager();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client offline " + ctx.channel().id() + ", isOpen=" + ctx.channel().isOpen() + ", isActive=" + ctx.channel().isActive() + ", isRegistered=" + ctx.channel().isRegistered());
        if(ctx.channel().isRegistered()){
            ctx.channel().deregister();
        }
        channelManager.remove(ctx.channel());
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelManager.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("threadId=" + Thread.currentThread().getId());

        if(msg instanceof Heartbeat){
            ctx.writeAndFlush(msg);
            return;
        }

        Request request = (Request) msg;

        Invoker invoker = createInvokers();


        Response response = invoker.invoke(request);

        ctx.writeAndFlush(response);

    }

    private Invoker createInvokers(){

        Invoker last = null;
        for(int i=filters.size() - 1; i>=0; i--){
            final InvokeFilter filter = filters.get(i);
            logger.info("filter:"+filter.getClass().getName());
            final Invoker next = last;
            last = new Invoker() {
                @Override
                public Response invoke(Request request) {
                    Response response = null;
                    try {
                        response = filter.invoke(next, request);
                    } catch (RpcException e) {
                        logger.error(e.getMessage(), e);
                    }
                    return response;
                }
            };
        }
        return last;
    }
}