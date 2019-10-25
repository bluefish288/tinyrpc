package com.tinyrpc.remoting.protocal;

import com.tinyrpc.common.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NioProtocolHandler extends AbstractProtocolHandler implements ProtocolHandler<ByteBuffer> {

    private final static Logger logger = LoggerFactory.getLogger(NioProtocolHandler.class);
    @Override
    public Message decodeMessage(ByteBuffer in) {

        if (in.limit() < INITIAL_BYTE_LENGTH) {
            return null;
        }

        // 魔数
        short magicData = in.getShort(0);
        if (magicData != MAGIC_DATA) {
            logger.info("magicData invalid =" + magicData);
            in.clear();
            return null;
        }

        in.mark();

        in.position(in.position()+MAGIC_DATA_LENGTH);

        Message message = Message.build();

        // 版本
        byte version = in.get();
        message.setVersion(version);

        // 消息id
        long messageId = in.getLong();
        message.setMessageId(messageId);

        // 状态位
        byte stat = in.get();

        if (ByteUtil.isBitSet(stat, (short) 0)) {
            message.setHeartbeat(true);
        }

        if(ByteUtil.isBitSet(stat, (short) 1)){
            message.setHasException(true);
        }

        if(ByteUtil.isBitSet(stat, (short) 2)){
            message.setZip(true);
        }

        if (in.remaining() < DATA_LENGTH_SIZE) {
            in.reset();
            return null;
        }

        int length = in.getInt();
        message.setLength(length);

        if (in.remaining() < length) {
            in.reset();
            return null;
        }

        byte[] body = new byte[length];

        in.get(body);

        if(message.isZip()){
            try {
                body = ByteUtil.unGzip(body);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }

        message.setBody(body);

        return message;
    }
}
