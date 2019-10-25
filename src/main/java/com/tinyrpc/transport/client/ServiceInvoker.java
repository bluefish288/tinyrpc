package com.tinyrpc.transport.client;

import com.tinyrpc.proxy.jdk.JdkProxyFactory;
import com.tinyrpc.proxy.ProxyFactory;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.transport.cluster.ClusterFactory;
import com.tinyrpc.transport.cluster.Cluster;
import com.tinyrpc.transport.loadbalance.LoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceInvoker {

    private final static Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);

    private final Map<Class, Object> proxyObjectMap = new ConcurrentHashMap<>();

    private final ProxyFactory proxyFactory = new JdkProxyFactory();

    private final List<Cluster> clusters = new ArrayList<>();

    private final Lock lock = new ReentrantLock();

    private Discovery discovery;


    public ServiceInvoker(Discovery discovery) {
        this.discovery = discovery;
    }

    public <T> T getService(Class<T> interCls) {

        InvokeConfig<T> invokeConfig = new InvokeConfig<>();
        invokeConfig.setInterCls(interCls);

        return getService(invokeConfig);
    }

    public <T> T getService(Class<T> interCls, LoadBalance.Type loadBalanceType) {

        InvokeConfig<T> invokeConfig = new InvokeConfig<>();
        invokeConfig.setInterCls(interCls);
        invokeConfig.setLoadBalanceType(loadBalanceType);

        return getService(invokeConfig);
    }

    public <T> T getService(InvokeConfig<T> invokeConfig) {

        Class<T> interCls = invokeConfig.getInterCls();
        Object service = proxyObjectMap.get(interCls);

        if (null == service) {
            lock.lock();
            try {
                service = proxyObjectMap.get(interCls);
                if (null != service) {
                    return (T) service;
                }

                Cluster cluster = ClusterFactory.INSTANCE.newCluster(invokeConfig, discovery);

                clusters.add(cluster);

                service = proxyFactory.getProxy(invokeConfig, cluster);

                proxyObjectMap.putIfAbsent(interCls, service);

                logger.info("createService:" + interCls.getName() + "=" + service.getClass().getName());

            } finally {
                lock.unlock();
            }
        }
        return (T) service;
    }

    public void close() {
        proxyObjectMap.clear();

        for (Cluster cluster : clusters) {
            cluster.close();
        }

    }
}
