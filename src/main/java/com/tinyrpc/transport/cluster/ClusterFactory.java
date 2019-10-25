package com.tinyrpc.transport.cluster;

import com.tinyrpc.registry.Constants;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.transport.client.InvokeConfig;

public class ClusterFactory {

    private ClusterFactory(){

    }

    public final static ClusterFactory INSTANCE = new ClusterFactory();

    public Cluster newCluster(InvokeConfig<?> invokeConfig, Discovery discovery){
        Cluster.Type clusterType = invokeConfig.getClusterType();
        Cluster cluster = null;
        if(null == clusterType){
            cluster = new FailfastCluster(invokeConfig, discovery);
        }else{
            switch (clusterType){
                case failfast:
                    cluster = new FailfastCluster(invokeConfig, discovery);
                    break;
                case failover:
                    cluster = new FailoverCluster(invokeConfig, discovery);
                    break;

            }
        }

        if(null == cluster){
            cluster = new FailfastCluster(invokeConfig, discovery);
        }

        return cluster;

    }

}