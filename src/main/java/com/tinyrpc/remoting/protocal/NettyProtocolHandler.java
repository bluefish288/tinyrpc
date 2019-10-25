package com.tinyrpc.remoting.protocal;

import com.tinyrpc.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NettyProtocolHandler extends AbstractProtocolHandler implements ProtocolHandler<ByteBuf> {

    private final static Logger logger = LoggerFactory.getLogger(NettyProtocolHandler.class);

    @Override
    public Message decodeMessage(ByteBuf in) {
        if (in.readableBytes() < INITIAL_BYTE_LENGTH) {
            return null;
        }

        // 魔数
        short magicData = in.getShort(0);
        if (magicData != MAGIC_DATA) {
            logger.info("magicData invalid =" + magicData);
            in.clear();
            return null;
        }

        in.markReaderIndex();

        in.skipBytes(MAGIC_DATA_LENGTH);

        Message message = Message.build();

        // 版本
        byte version = in.readByte();
        message.setVersion(version);

        // 消息id
        long messageId = in.readLong();
        message.setMessageId(messageId);

        // 状态位
        byte stat = in.readByte();

        if (ByteUtil.isBitSet(stat, (short) 0)) {
            message.setHeartbeat(true);
        }

        if(ByteUtil.isBitSet(stat, (short) 1)){
            message.setHasException(true);
        }

        if(ByteUtil.isBitSet(stat, (short) 2)){
            message.setZip(true);
        }

        if (in.readableBytes() < DATA_LENGTH_SIZE) {
            in.resetReaderIndex();
            return null;
        }

        int length = in.readInt();
        message.setLength(length);

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return null;
        }

        byte[] body = new byte[length];

        in.readBytes(body, 0, length);

        if(message.isZip()){
            try {
                body = ByteUtil.unGzip(body);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }

        message.setBody(body);

        if(in.isReadable()){
            in.discardReadBytes();
        }

        return message;
    }



}
