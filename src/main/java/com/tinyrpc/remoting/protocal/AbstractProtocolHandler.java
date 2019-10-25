package com.tinyrpc.remoting.protocal;

import com.tinyrpc.common.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 魔数 2字节
 * 版本号 1字节
 * 消息id: 8字节
 * 状态位 1字节: 0:是否是heartbeat, 1:是否Exception 2: 是否gzip
 * 消息长度: 4字节
 * body体:长度为消息长度
 */
public abstract class AbstractProtocolHandler {

    private final static Logger logger = LoggerFactory.getLogger(AbstractProtocolHandler.class);


    protected final static short MAGIC_DATA = 0x7862;

    protected final static int MAGIC_DATA_LENGTH = 2;

    protected final static int VERSION_LENGTH = 1;

    protected final static int MESSAGE_ID_LENGTH = 8;

    protected final static int STATE_LENGTH = 1;

    protected final static int DATA_LENGTH_SIZE = 4;

    protected final static int INITIAL_BYTE_LENGTH = (MAGIC_DATA_LENGTH + VERSION_LENGTH + MESSAGE_ID_LENGTH + STATE_LENGTH);


    protected final static int GZIP_THRESHOLD = 512;

    public byte[] encodeMessage(Message message) {

        byte[] bytes = new byte[INITIAL_BYTE_LENGTH];

        int offset = 0;

        // 魔数
        ByteUtil.short2bytes(MAGIC_DATA, bytes, offset);
        offset += MAGIC_DATA_LENGTH;

        // 版本号
        bytes[offset++] = message.getVersion();

        // 消息id
        ByteUtil.long2bytes(message.getMessageId(), bytes, offset);
        offset += MESSAGE_ID_LENGTH;

        // 状态位
        byte stat = (byte) 0x00;

        if (message.isHeartbeat()) {
            stat = ByteUtil.setBit(stat, (short) 0);
        }

        if(message.isHasException()){
            stat = ByteUtil.setBit(stat, (short) 1);
        }

        // body 长度
        byte[] body = message.getBody();
        int length = body.length;

        boolean zip = false;

        logger.info("before zip length = " + length);
        if(length > GZIP_THRESHOLD){
            try {
                body = ByteUtil.gzip(body);
                length = body.length;
                zip = true;
                logger.info("zipped " + length);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }

        if(zip){
            message.setZip(true);
            stat = ByteUtil.setBit(stat, (short) 2);
        }

        bytes[offset++] = stat;

        message.setLength(length);

        bytes = resizeBytes(bytes, DATA_LENGTH_SIZE + length);

        // 写入 body length
        ByteUtil.int2bytes(length, bytes, offset);
        offset += DATA_LENGTH_SIZE;


        // 写入 body
        System.arraycopy(body, 0, bytes, offset, length);

        return bytes;
    }

    private byte[] resizeBytes(byte[] bytes, int increment) {
        byte[] res = new byte[bytes.length + increment];
        System.arraycopy(bytes, 0, res, 0, bytes.length);
        return res;
    }
}
