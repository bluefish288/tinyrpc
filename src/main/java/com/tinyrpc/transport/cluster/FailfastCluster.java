package com.tinyrpc.transport.cluster;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.Client;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.loadbalance.LoadBalance;

import java.util.List;

public class FailfastCluster extends AbstractCluster {

    public FailfastCluster(InvokeConfig<?> invokeConfig, Discovery discovery) {
        super(invokeConfig, discovery);
    }

    @Override
    public ResponseFuture doSend(final Request request, final List<Client> clients, final LoadBalance loadBalance, final InvokeConfig<?> invokeConfig) throws InterruptedException, RpcException {

        Client client = loadBalance.select(clients, request);

        ResponseFuture future = client.send(request);
        future.setTimeout(invokeConfig.getTimeout());
        return future;
    }
}