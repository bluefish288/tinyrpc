package com.tinyrpc.transport.cluster;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.Constants;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.Client;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.loadbalance.LoadBalance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FailoverCluster extends AbstractCluster {


    public FailoverCluster(InvokeConfig<?> invokeConfig, Discovery discovery) {
        super(invokeConfig, discovery);
    }

    @Override
    protected ResponseFuture doSend(final Request request, final List<Client> clients, final LoadBalance loadBalance, final InvokeConfig<?> invokeConfig) throws InterruptedException, RpcException {

        Throwable lastError = null;

        int retries = invokeConfig.getRetries();
        if (retries <= 0) {
            retries = Constants.DEFAULT_RETRY_COUNT;
        }
        int maxInvokeTimes = retries + 1;

        ResponseFuture future = null;

        List<Client> clientsToSelect = new CopyOnWriteArrayList<>(clients);
        for (int index = 0; index < maxInvokeTimes; index++) {
            Client client = loadBalance.select(clientsToSelect, request);
            try {
                future = client.send(request);
                future.setTimeout(invokeConfig.getTimeout());
                return future;
            } catch (Exception e) {
                lastError = e;
                clientsToSelect.remove(client);
            }
        }

        future = new ResponseFuture();
        future.setValue(lastError, true);
        return future;
    }
}
