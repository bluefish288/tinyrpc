package com.tinyrpc.transport.client;

import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.remoting.exchange.ResponseFuture;

public interface ResponseHandler {

    public void handle(Response response);

    public void put(Request request, ResponseFuture responseFuture);
}