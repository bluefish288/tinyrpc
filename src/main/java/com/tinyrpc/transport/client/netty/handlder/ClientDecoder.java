package com.tinyrpc.transport.client.netty.handlder;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.MessageIdGenerator;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.remoting.exchange.Heartbeat;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientDecoder extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(ClientDecoder.class);

    private ExchangeCodec<Response> responseCodec = CodecFactory.newResponseCodec();

    private ExchangeCodec<Heartbeat> heartbeatCodec = CodecFactory.newHeartbeatCodec();

    private ProtocolHandler<ByteBuf> protocolHandler = new NettyProtocolHandler();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


        if (in.readableBytes() <= 0) {
            return;
        }

        Message message = protocolHandler.decodeMessage(in);

        if(null == message){
            return;
        }

        byte[] body = message.getBody();

        if(message.isHeartbeat()){
            if(logger.isDebugEnabled()){
                logger.debug("----- receive heartbeat -> "+message.getMessageId());
            }

            Heartbeat heartbeat = heartbeatCodec.decode(message.getBody());

            if(logger.isDebugEnabled()){
                logger.debug(String.format("heartbeat -> sendTime=%d, receiveTime=%d", heartbeat.getSendTime(), heartbeat.getReceiveTime()));
            }

            return;
        }



        Response response = responseCodec.decode(body);
        response.setMessageId(message.getMessageId());

        out.add(response);

        logger.info(String.valueOf(in.refCnt()));


    }

    // 处理heartbeat
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){

            Message message = Message.build();
            message.setMessageId(MessageIdGenerator.generateMessageId());
            message.setHeartbeat(true);

            Heartbeat heartbeat = new Heartbeat();
            heartbeat.setMessageId(message.getMessageId());
            heartbeat.setSendTime(System.currentTimeMillis());

            message.setBody(heartbeatCodec.encode(heartbeat));

            byte[] bytes = protocolHandler.encodeMessage(message);

            ByteBuf buf = ctx.alloc().buffer(bytes.length);
            buf.writeBytes(bytes);

            ctx.writeAndFlush(buf);

            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()){
                case READER_IDLE:
                    logger.info("------ READER_IDLE");
                    break;
                case WRITER_IDLE:
                    logger.info("------ WRITE_IDLE");
                    break;
                case ALL_IDLE:
                    logger.info("------ ALL_IDLE");
                    break;
            }
        }

        super.userEventTriggered(ctx, evt);
    }
}