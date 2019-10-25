package com.tinyrpc.transport.client;

import com.tinyrpc.registry.Constants;
import com.tinyrpc.transport.cluster.Cluster;
import com.tinyrpc.transport.loadbalance.LoadBalance;

public class InvokeConfig<T> {

    private Class<T> interCls;

    private Cluster.Type clusterType;

    private LoadBalance.Type loadBalanceType;

    private Integer retries = Constants.DEFAULT_RETRY_COUNT;

    private Long timeout = Constants.DEFAULT_INVOKE_TIMEOUT;

    private String group = Constants.DEFAULT_GROUP;

    private String version = Constants.DEFAULT_VERSION;


    public Class<T> getInterCls() {
        return interCls;
    }

    public void setInterCls(Class<T> interCls) {
        this.interCls = interCls;
    }

    public Cluster.Type getClusterType() {
        return clusterType;
    }

    public void setClusterType(Cluster.Type clusterType) {
        this.clusterType = clusterType;
    }

    public LoadBalance.Type getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(LoadBalance.Type loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        if(null == retries){
            return;
        }
        this.retries = retries;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        if(null == timeout){
            return;
        }
        this.timeout = timeout;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
