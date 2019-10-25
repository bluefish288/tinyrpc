package com.tinyrpc.remoting.protocal;

public interface ProtocolHandler<T> {

    public byte[] encodeMessage(Message message);

    public Message decodeMessage(T in);
}
