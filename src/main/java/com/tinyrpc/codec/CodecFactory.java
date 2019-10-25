package com.tinyrpc.codec;

import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.codec.exchange.HeartbeatCodec;
import com.tinyrpc.codec.exchange.RequestCodec;
import com.tinyrpc.codec.exchange.ResponseCodec;
import com.tinyrpc.remoting.exchange.Heartbeat;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;

public class CodecFactory {

    public static ExchangeCodec<Heartbeat> newHeartbeatCodec(){
        return new HeartbeatCodec();
    }

    public static ExchangeCodec<Request> newRequestCodec(){
        return new RequestCodec();
    }

    public static ExchangeCodec<Response> newResponseCodec(){
        return new ResponseCodec();
    }
}