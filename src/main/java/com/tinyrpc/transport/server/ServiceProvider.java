package com.tinyrpc.transport.server;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.Registry;
import com.tinyrpc.registry.RegistryFactory;
import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.registry.URL;
import com.tinyrpc.common.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProvider {

    private final static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

    private ConcurrentHashMap<Integer, Server> serverMap = new ConcurrentHashMap<>();

    private ServiceContext serviceContext = ServiceContext.INSTANCE;

    private Registry registry;

    public ServiceProvider(String connectStr) {
        this.registry = RegistryFactory.getRegistry(connectStr);
    }

    public void export(ServiceConfig serviceConfig) {

        if(serviceConfig.getPort() == 0){
            serviceConfig.setPort(NetUtils.getAvailablePort());
        }

        int port = serviceConfig.getPort();
        Class<?> interCls = serviceConfig.getInterCls();

        if (!serverMap.containsKey(port)) {

            Server server = ServerFactory.newServer().setRequestProcessor(new ServiceProcessor());

            ServerConfig serverConfig = new ServerConfig();
            serverConfig.setPort(port);


            try {
                server.start(serverConfig);
            } catch (RpcException e) {
                logger.error(e.getMessage(), e);
            }

            serverMap.put(port, server);
        }

        try {
            serviceContext.addService(serviceConfig);
        } catch (RpcException e) {
            logger.error(e.getMessage(),e);
        }

        if (null != registry) {

            String serviceName = interCls.getName();

            URL url = new URL(NetUtils.getLocalHost(), port, serviceName, serviceConfig.getGroup(),serviceConfig.getVersion(), serviceConfig.getWeight());

            ServiceInfo serviceInfo = new ServiceInfo(url.getHost(), url.getPort(), url.getWeight());

            registry.register(url, serviceInfo);

        }

    }

    public void close(){
        for(InvokeKey invokeKey : serviceContext.getInvokeKeys()){
            for(int port : serverMap.keySet()){
                URL url = new URL(NetUtils.getLocalHost(), port, invokeKey.getServiceName(), invokeKey.getGroup(),invokeKey.getVersion());
                registry.unregister(url);
            }
        }

        for(Server server : serverMap.values()){
            server.close();
        }
    }
}