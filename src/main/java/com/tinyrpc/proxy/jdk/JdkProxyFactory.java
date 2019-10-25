package com.tinyrpc.proxy.jdk;

import com.tinyrpc.proxy.ProxyFactory;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.cluster.Cluster;

import java.lang.reflect.Proxy;

public class JdkProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(InvokeConfig<T> invokeConfig, Cluster cluster) {
        Class<T> clz = invokeConfig.getInterCls();
        return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new ReferInvocationHandler(invokeConfig, cluster));
    }
}