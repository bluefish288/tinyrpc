package com.tinyrpc.transport.server.netty.filter;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;

public interface InvokeFilter {

    public Response invoke(Invoker invoker, Request request) throws RpcException;

}
