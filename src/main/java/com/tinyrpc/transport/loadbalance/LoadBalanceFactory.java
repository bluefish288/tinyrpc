package com.tinyrpc.transport.loadbalance;

public class LoadBalanceFactory {

    public static LoadBalance newLoadBalance(LoadBalance.Type loadBalanceType){
        if(null == loadBalanceType){
            return new RoundRobinLoadBalance();
        }
        if(loadBalanceType == LoadBalance.Type.roundrobin){
            return new RoundRobinLoadBalance();
        }
        if(loadBalanceType == LoadBalance.Type.random){
            return new RandomLoadBalance();
        }
        return new RoundRobinLoadBalance();
    }
}
