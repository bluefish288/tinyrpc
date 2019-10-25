package com.tinyrpc.transport.loadbalance;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.client.Client;
import com.tinyrpc.transport.client.WeightManager;

import java.util.List;
import java.util.Map;

public abstract class AbstractLoadBalance implements LoadBalance {

    private WeightManager weightManager = WeightManager.INSTANCE;

    @Override
    public Client select(List<Client> clients, Request request) throws RpcException {
        if(null == clients || clients.size() == 0){
            throw new RpcException("no client exist for "+request.getInterfaceName(), RpcException.NO_INVOKER_AVAILABLE);
        }
        if(clients.size() == 1){
            return clients.get(0);
        }
        int[] weights = new int[clients.size()];
        Map<String,Integer> weightMap = weightManager.getWeights(request);
        for(int i=0;i<clients.size();i++){
            Client client = clients.get(i);
            String key = client.getRemoteHost()+"."+client.getRemotePort();
            weights[i] = weightMap.getOrDefault(key,ServiceInfo.DEFAULT_WEIGHT);
        }

        return doSelect(clients, weights, request);
    }

    protected abstract Client doSelect(List<Client> clients, int[] weights, Request request);

}
