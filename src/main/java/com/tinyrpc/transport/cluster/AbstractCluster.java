package com.tinyrpc.transport.cluster;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.registry.ServiceListener;
import com.tinyrpc.registry.URL;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.Client;
import com.tinyrpc.transport.client.ClientManager;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.client.WeightManager;
import com.tinyrpc.transport.loadbalance.LoadBalance;
import com.tinyrpc.transport.loadbalance.LoadBalanceFactory;
import com.tinyrpc.transport.server.InvokeKey;

import java.util.List;

public abstract class AbstractCluster implements Cluster {

    private String serviceName;

    private LoadBalance loadBalance;

    private ClientManager clientManager = ClientManager.INSTANCE;

    private ServiceListener[] serviceListeners = new ServiceListener[]{clientManager, WeightManager.INSTANCE};

    private Discovery discovery;

    private InvokeConfig<?> invokeConfig;

    private URL url;

    public AbstractCluster(InvokeConfig<?> invokeConfig, Discovery discovery){
        this.serviceName = invokeConfig.getInterCls().getName();
        this.loadBalance = LoadBalanceFactory.newLoadBalance(invokeConfig.getLoadBalanceType());
        this.discovery = discovery;
        this.invokeConfig = invokeConfig;
        this.url = new URL(invokeConfig.getInterCls().getName(), invokeConfig.getGroup(), invokeConfig.getVersion());

        for(ServiceListener listener : serviceListeners){
            discovery.subscribe(url, listener);
        }
    }

    @Override
    public ResponseFuture send(Request request) throws InterruptedException, RpcException {
        List<Client> clients = clientManager.getClients(new InvokeKey(request.getGroup(), request.getInterfaceName(),request.getVersion()));
        if(clients.size() == 0){
            throw new RpcException(RpcException.NO_SERVICE_EXIST);
        }
        return this.doSend(request, clients, loadBalance, invokeConfig);
    }

    protected abstract ResponseFuture doSend(final Request request, final List<Client> clients, final LoadBalance loadBalance, final InvokeConfig<?> invokeConfig) throws InterruptedException, RpcException;

    @Override
    public void close(){
        for(ServiceListener listener : serviceListeners){
            discovery.unsubscribe(url, listener);
        }
    }

}