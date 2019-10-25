package com.tinyrpc.spring;

import com.tinyrpc.registry.zk.ZookeeperDiscovery;
import com.tinyrpc.transport.client.ServiceInvoker;
import org.springframework.beans.factory.InitializingBean;

public class DiscoverBean implements InitializingBean {

    private String connectStr;

    private ServiceInvoker serviceInvoker;

    public void setConnectStr(String connectStr) {
        this.connectStr = connectStr;
    }

    public ServiceInvoker getServiceInvoker() {
        return serviceInvoker;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        serviceInvoker = new ServiceInvoker(new ZookeeperDiscovery(connectStr));
    }
}
