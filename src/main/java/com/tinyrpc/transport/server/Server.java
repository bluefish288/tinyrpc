package com.tinyrpc.transport.server;

import com.tinyrpc.common.exception.RpcException;

public interface Server {

    public Server setRequestProcessor(ServiceProcessor serviceProcessor);

    public void start(ServerConfig serverConfig) throws RpcException;

    public void close();

}