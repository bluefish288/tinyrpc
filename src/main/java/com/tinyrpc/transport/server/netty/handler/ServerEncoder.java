package com.tinyrpc.transport.server.netty.handler;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.remoting.exchange.Exchange;
import com.tinyrpc.remoting.exchange.Heartbeat;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerEncoder extends MessageToByteEncoder<Exchange> {

    private final static Logger logger = LoggerFactory.getLogger(ServerEncoder.class);

    private ExchangeCodec<Response> responseCodec = CodecFactory.newResponseCodec();

    private ExchangeCodec<Heartbeat> heartbeatCodec = CodecFactory.newHeartbeatCodec();

    private ProtocolHandler<ByteBuf> protocolHandler = new NettyProtocolHandler();

    @Override
    protected void encode(ChannelHandlerContext ctx, Exchange msg, ByteBuf out) throws Exception {

        if(msg instanceof Response){
            Response response = (Response) msg;
            byte[] body = responseCodec.encode(response);

            Message message = Message.build();
            message.setMessageId(msg.getMessageId());
            message.setLength(body.length);
            message.setBody(body);


            if(response.isHasException()){
                message.setHasException(true);
            }

            byte[] result = protocolHandler.encodeMessage(message);

            out.writeBytes(result);

            logger.info(String.valueOf(out.refCnt()));
        }else if(msg instanceof Heartbeat){
            // heartbeat
            Heartbeat heartbeat = (Heartbeat) msg;

            Message message = Message.build();
            message.setMessageId(msg.getMessageId());
            message.setHeartbeat(true);

            logger.info(String.valueOf(heartbeat));

            byte[] body = heartbeatCodec.encode(heartbeat);
            message.setBody(body);

            byte[] result = protocolHandler.encodeMessage(message);
            out.writeBytes(result);
        }



    }
}