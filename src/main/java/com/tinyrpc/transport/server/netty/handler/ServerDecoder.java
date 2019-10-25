package com.tinyrpc.transport.server.netty.handler;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.remoting.exchange.Exchange;
import com.tinyrpc.remoting.exchange.Heartbeat;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerDecoder extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(ServerDecoder.class);

    private ExchangeCodec<Request> requestCodec = CodecFactory.newRequestCodec();

    private ExchangeCodec<Heartbeat> heartbeatCodec = CodecFactory.newHeartbeatCodec();

    private ProtocolHandler<ByteBuf> protocolHandler = new NettyProtocolHandler();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        logger.info("ServerDecoder " + in.readableBytes());



        if (in.readableBytes() <= 0) {
            logger.info(" no messages ");
            return;
        }

        Message message = protocolHandler.decodeMessage(in);

        if(null == message){
            return;
        }

        Exchange exchange = null;

        if(message.isHeartbeat()){
            logger.info("heartbeat -> " + message.getMessageId());

            Heartbeat heartbeat = heartbeatCodec.decode(message.getBody());
            heartbeat.setMessageId(message.getMessageId());

            heartbeat.setReceiveTime(System.currentTimeMillis());

            exchange = heartbeat;

        }else{
            exchange = requestCodec.decode(message.getBody());
            exchange.setMessageId(message.getMessageId());
        }


        out.add(exchange);

        ctx.flush();


    }
}