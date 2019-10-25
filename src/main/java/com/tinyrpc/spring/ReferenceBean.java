package com.tinyrpc.spring;

import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.client.ServiceInvoker;
import com.tinyrpc.transport.cluster.Cluster;
import com.tinyrpc.transport.loadbalance.LoadBalance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ReferenceBean implements FactoryBean , InitializingBean, ApplicationContextAware {

    private Class<?> interfaceClass;
    private String cluster;
    private String loadbalance;
    private Integer retries;

    private String group;

    private String version;

    private ApplicationContext applicationContext;
    private ServiceInvoker serviceInvoker;

    @Override
    public Object getObject() throws Exception {
        InvokeConfig invokeConfig = new InvokeConfig<>();
        invokeConfig.setInterCls(interfaceClass);
        invokeConfig.setClusterType(Cluster.Type.byNameWithDefault(cluster));
        invokeConfig.setLoadBalanceType(LoadBalance.Type.byNameWithDefault(loadbalance));
        if(null!=retries){
            invokeConfig.setRetries(retries);
        }

        if(StringUtils.isNotBlank(group)){
            invokeConfig.setGroup(group);
        }
        if(StringUtils.isNotBlank(version)){
            invokeConfig.setVersion(version);
        }

        return this.serviceInvoker.getService(invokeConfig);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        DiscoverBean discoverBean = this.applicationContext.getBean(DiscoverBean.class);
        if(null == discoverBean){
            throw new RuntimeException("discoverBean is null");
        }
        this.serviceInvoker = discoverBean.getServiceInvoker();
    }
}
