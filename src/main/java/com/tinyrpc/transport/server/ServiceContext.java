package com.tinyrpc.transport.server;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceContext {

    private ServiceContext(){}

    public static ServiceContext INSTANCE = new ServiceContext();

    private Map<InvokeKey, ServiceConfig> serviceCache = new ConcurrentHashMap<>();


    public void addService(ServiceConfig serviceConfig) throws RpcException {

        if (!valid(serviceConfig)) {
            throw new RpcException("invalid serviceConfig : "+serviceConfig.toString(), RpcException.CONFIG_ERROR);
        }

        serviceCache.put(new InvokeKey(serviceConfig.getGroup(), serviceConfig.getInterCls().getName(), serviceConfig.getVersion()), serviceConfig);

    }

    public Collection<InvokeKey> getInvokeKeys(){
        return serviceCache.keySet();
    }

    public Object getService(Request request){
        InvokeKey key = new InvokeKey(request.getGroup(), request.getInterfaceName(), request.getVersion());
        ServiceConfig serviceConfig = serviceCache.get(key);
        if(null == serviceConfig){
            throw new RpcException(RpcException.CONFIG_ERROR);
        }
        Object service = serviceConfig.getService();
        if(null == service){
            throw new RpcException(RpcException.CONFIG_ERROR);
        }
        return service;
    }

    private boolean valid(ServiceConfig serviceConfig) {
        String serviceName = serviceConfig.getInterCls().getName();
        Class<?>[] serviceInters = serviceConfig.getService().getClass().getInterfaces();
        if(serviceInters.length == 0){
            return false;
        }
        for (Class<?> interfaceClass : serviceInters) {
            if (serviceName.equals(interfaceClass.getName())) {
                return true;
            }
        }
        return false;
    }

}