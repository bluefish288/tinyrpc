package com.tinyrpc.registry;

import com.tinyrpc.transport.server.InvokeKey;

import java.util.List;

public interface ServiceListener {

    public void onServiceRefresh(InvokeKey invokeKey, List<ServiceInfo> serviceInfos);

    public void onServiceRemove(InvokeKey invokeKey);
}