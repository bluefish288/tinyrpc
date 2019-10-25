package com.tinyrpc.codec.exchange;

import com.tinyrpc.codec.serialize.Hessian2Serialization;
import com.tinyrpc.codec.Serialization;
import com.tinyrpc.remoting.exchange.Response;

import java.io.IOException;

public class ResponseCodec implements ExchangeCodec<Response> {

    private Serialization serialization = new Hessian2Serialization();

    @Override
    public byte[] encode(Response response) throws IOException {
        return serialization.serialize(response);
    }

    @Override
    public Response decode(byte[] bytes) throws IOException {
        return serialization.deserialize(bytes, Response.class);
    }
}