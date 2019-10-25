package com.tinyrpc.registry;

public interface Discovery {

    public void subscribe(URL url, ServiceListener listener);

    public void unsubscribe(URL url, ServiceListener listener);

}