package com.tinyrpc.registry;

import com.tinyrpc.registry.zk.ZookeeperDiscovery;
import com.tinyrpc.registry.zk.ZookeeperRegistry;

import java.util.HashMap;
import java.util.Map;

public class RegistryFactory {

    private static Map<String,Registry> registryMap = new HashMap<>();

    private static Map<String,Discovery> discoveryMap = new HashMap<>();

    public static Registry getRegistry(String connectStr){
        Registry registry = registryMap.get(connectStr);
        if(null == registry){
            registry = new ZookeeperRegistry(connectStr);
            registryMap.put(connectStr, registry);
        }
        return registry;
    }

    public static Discovery getDiscovery(String connectStr){
        Discovery discovery = discoveryMap.get(connectStr);
        if(null == discovery){
            discovery = new ZookeeperDiscovery(connectStr);
            discoveryMap.put(connectStr, discovery);
        }
        return discovery;
    }
}
