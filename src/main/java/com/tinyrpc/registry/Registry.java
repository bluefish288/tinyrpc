package com.tinyrpc.registry;

public interface Registry {

    public void register(URL url, ServiceInfo serviceInfo);

    public void unregister(URL url);

}