package com.tinyrpc.transport.client;

import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.registry.ServiceListener;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.server.InvokeKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeightManager implements ServiceListener {

    private Map<InvokeKey, ConcurrentHashMap<String, Integer>> weightMap = new ConcurrentHashMap<>();

    public final static WeightManager INSTANCE = new WeightManager();

    @Override
    public void onServiceRefresh(InvokeKey invokeKey, List<ServiceInfo> serviceInfos) {
        ConcurrentHashMap<String, Integer> map = weightMap.get(invokeKey);
        if (null == map) {
            weightMap.putIfAbsent(invokeKey, new ConcurrentHashMap<>());
            map = weightMap.get(invokeKey);
        }
        for (ServiceInfo serviceInfo : serviceInfos) {
            map.put(getKey(serviceInfo), serviceInfo.getWeight());
        }
    }

    @Override
    public void onServiceRemove(InvokeKey invokeKey) {
        ConcurrentHashMap<String, Integer> map = weightMap.get(invokeKey);
        if (null != map) {
            map.clear();
            map = null;
        }
        weightMap.remove(invokeKey);
    }

    public Map<String, Integer> getWeights(Request request) {
        if (null == request) {
            return Collections.emptyMap();
        }
        InvokeKey invokeKey = new InvokeKey(request.getGroup(), request.getInterfaceName(), request.getVersion());
        ConcurrentHashMap<String, Integer> map = weightMap.get(invokeKey);
        if (null == map || map.size() == 0) {
            return Collections.emptyMap();
        }
        return new HashMap<>(map);
    }

    private String getKey(ServiceInfo serviceInfo) {
        return serviceInfo.getHost() + "." + serviceInfo.getPort();
    }
}
