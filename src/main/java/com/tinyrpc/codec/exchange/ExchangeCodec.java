package com.tinyrpc.codec.exchange;

import com.tinyrpc.remoting.exchange.Exchange;

import java.io.IOException;

public interface ExchangeCodec<T extends Exchange> {

    public byte[] encode(T t) throws IOException;

    public T decode(byte[] bytes) throws IOException;
}