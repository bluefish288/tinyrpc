
package com.tinyrpc.proxy;

import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.cluster.Cluster;

public interface ProxyFactory {

    public <T> T getProxy(InvokeConfig<T> invokeConfig, Cluster cluster);

}
