package com.tinyrpc.transport.server.netty.filter;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.transport.server.ServiceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceProcessFilter implements InvokeFilter {

    private final static Logger logger = LoggerFactory.getLogger(ServiceProcessFilter.class);

    private ServiceProcessor serviceProcessor;

    public ServiceProcessFilter(ServiceProcessor serviceProcessor) {
        this.serviceProcessor = serviceProcessor;
    }

    @Override
    public Response invoke(Invoker invoker, Request request) throws RpcException {

        logger.info(String.valueOf(invoker));

        Response response = new Response();
        response.setMessageId(request.getMessageId());
        Object value = serviceProcessor.getValue(request);
        response.setValue(value);

        if(value instanceof Throwable){
            response.setHasException(true);
        }

        return response;
    }
}
