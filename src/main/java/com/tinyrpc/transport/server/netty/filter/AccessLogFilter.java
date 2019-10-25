package com.tinyrpc.transport.server.netty.filter;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogFilter implements InvokeFilter {

    private final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);
    @Override
    public Response invoke(Invoker invoker, Request request) throws RpcException {
        logger.info(invoker.getClass().getName());
        return invoker.invoke(request);
    }
}
