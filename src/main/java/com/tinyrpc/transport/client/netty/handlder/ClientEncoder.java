package com.tinyrpc.transport.client.netty.handlder;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEncoder extends MessageToByteEncoder<Request> {

    private final static Logger logger = LoggerFactory.getLogger(ClientEncoder.class);

    private ExchangeCodec<Request> requestCodec = CodecFactory.newRequestCodec();

    private ProtocolHandler<ByteBuf> protocolHandler = new NettyProtocolHandler();

    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {


        Message message = Message.build();
        message.setMessageId(msg.getMessageId());

        if(logger.isDebugEnabled()){
            logger.debug(String.valueOf(msg));
        }

        byte[] body = requestCodec.encode(msg);

        message.setBody(body);

        byte[] bytes = protocolHandler.encodeMessage(message);


        out.writeBytes(bytes);

        if(logger.isDebugEnabled()){
            logger.debug(String.valueOf(out.refCnt()));
        }


    }
}