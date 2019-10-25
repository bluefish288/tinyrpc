package com.tinyrpc.transport.server.netty.filter;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;

public interface Invoker {

    public Response invoke(Request request) throws RpcException;

}
