package com.tinyrpc.transport.client;

import com.tinyrpc.transport.client.netty.NettyClient;

public class ClientFactory {

    public static Client newClient(String remoteHost, int remotePort){
        return new NettyClient(remoteHost, remotePort);
    }
}
