package com.tinyrpc.transport.server;

import com.tinyrpc.transport.server.netty.NettyServer;

public class ServerFactory {

    public static Server newServer(){
        return new NettyServer();
    }
}
