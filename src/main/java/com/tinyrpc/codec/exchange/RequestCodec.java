package com.tinyrpc.codec.exchange;


import com.tinyrpc.codec.serialize.Hessian2Serialization;
import com.tinyrpc.codec.Serialization;
import com.tinyrpc.remoting.exchange.Request;

import java.io.IOException;

public class RequestCodec  implements ExchangeCodec<Request>{

    private Serialization serialization = new Hessian2Serialization();

    @Override
    public byte[] encode(Request request) throws IOException {
        return serialization.serialize(request);
    }

    @Override
    public Request decode(byte[] bytes) throws IOException {
        return serialization.deserialize(bytes, Request.class);
    }
}