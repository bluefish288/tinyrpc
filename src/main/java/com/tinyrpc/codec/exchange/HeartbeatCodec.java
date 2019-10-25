package com.tinyrpc.codec.exchange;

import com.tinyrpc.remoting.exchange.Heartbeat;
import com.tinyrpc.common.util.ByteUtil;

import java.io.IOException;

public class HeartbeatCodec implements ExchangeCodec<Heartbeat> {

    private final static int LONG_VALUE_BYTE_COUNT = 8;

    @Override
    public byte[] encode(Heartbeat heartbeat) throws IOException {
        if (heartbeat.getMessageId() == 0) {
            return new byte[0];
        }

        int length = LONG_VALUE_BYTE_COUNT;
        if (heartbeat.getSendTime() > 0) {
            length += LONG_VALUE_BYTE_COUNT;

            if (heartbeat.getReceiveTime() > 0) {
                length += LONG_VALUE_BYTE_COUNT;
            }
        }

        byte[] bytes = new byte[length];

        ByteUtil.long2bytes(heartbeat.getMessageId(), bytes, 0);
        if (bytes.length >= LONG_VALUE_BYTE_COUNT * 2) {
            ByteUtil.long2bytes(heartbeat.getSendTime(), bytes, LONG_VALUE_BYTE_COUNT);
        }

        if (bytes.length == LONG_VALUE_BYTE_COUNT * 3) {
            ByteUtil.long2bytes(heartbeat.getReceiveTime(), bytes, LONG_VALUE_BYTE_COUNT * 2);
        }

        return bytes;
    }

    @Override
    public Heartbeat decode(byte[] bytes) throws IOException {
        int length = bytes.length;
        if (length < LONG_VALUE_BYTE_COUNT) {
            return null;
        }
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setMessageId(ByteUtil.bytes2long(bytes, 0));
        if (length >= LONG_VALUE_BYTE_COUNT * 2) {
            heartbeat.setSendTime(ByteUtil.bytes2long(bytes, LONG_VALUE_BYTE_COUNT));
        }
        if (length == LONG_VALUE_BYTE_COUNT * 3) {
            heartbeat.setReceiveTime(ByteUtil.bytes2long(bytes, LONG_VALUE_BYTE_COUNT * 2));
        }

        return heartbeat;
    }
}