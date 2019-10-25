package com.tinyrpc.transport.client;

import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.remoting.protocal.MessageIdGenerator;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultResponseHandler implements ResponseHandler {

    private final static Logger logger = LoggerFactory.getLogger(DefaultResponseHandler.class);

    protected ConcurrentMap<Long, ResponseFuture> callbackMap = new ConcurrentHashMap<>();

    @Override
    public void handle(Response response) {

        logger.info(String.valueOf(response.getMessageId()));
        ResponseFuture future = callbackMap.get(response.getMessageId());
        future.setValue(response.getValue(), response.isHasException());
        callbackMap.remove(response.getMessageId());
    }

    @Override
    public void put(Request request, ResponseFuture responseFuture) {
        if(request.getMessageId() == 0){
            request.setMessageId(MessageIdGenerator.generateMessageId());
        }
        callbackMap.put(request.getMessageId(), responseFuture);
    }
}