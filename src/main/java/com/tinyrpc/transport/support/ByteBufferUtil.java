package com.tinyrpc.transport.support;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ByteBufferUtil {

    public final static ByteBuffer read(SocketChannel channel, ByteBuffer buffer) throws IOException {
        while ((channel.read(buffer)) > 0){
            if(!buffer.hasRemaining()){
                buffer = grow(buffer);
            }
        }
        return buffer;
    }

    private final static ByteBuffer grow(ByteBuffer buf) {
        ByteBuffer newbuf = ByteBuffer.allocate(buf.capacity() * 2);
        newbuf.put(buf.array());
        newbuf.position(buf.position());
        return newbuf;
    }
}
