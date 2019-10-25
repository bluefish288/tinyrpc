package com.tinyrpc.transport.loadbalance;

import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RoundRobinLoadBalance extends AbstractLoadBalance{

    private final static Logger logger = LoggerFactory.getLogger(RoundRobinLoadBalance.class);

    private Map<String,ConcurrentHashMap<String,WeightedRoundRobin>> methodWeightMap = new ConcurrentHashMap<>();

    protected static class WeightedRoundRobin {
        private int weight;
        private AtomicLong current = new AtomicLong(0);

        public int getWeight() {
            return weight;
        }
        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }
        public long increaseCurrent() {
            return current.addAndGet(weight);
        }
        public void sel(int total) {
            current.addAndGet(-1 * total);
        }
    }

    @Override
    protected Client doSelect(List<Client> clients, int[] weights, Request request) {

//        String methodKey = request.getInterfaceName()+"."+request.getMethodName();
        String methodKey = request.getInterfaceName();
        ConcurrentHashMap<String, WeightedRoundRobin> roundRobinMap = methodWeightMap.get(methodKey);
        if(null == roundRobinMap){
            methodWeightMap.putIfAbsent(methodKey, new ConcurrentHashMap<>());
            roundRobinMap = methodWeightMap.get(methodKey);
        }

        int totalWeight = 0;
        long maxCurrent = Long.MIN_VALUE;
        Client selectedClient = null;
        WeightedRoundRobin selectedWRR = null;

        for(int i=0;i<clients.size();i++){
            Client client = clients.get(i);
            String key = client.getRemoteHost()+"."+client.getRemotePort();
            WeightedRoundRobin weightedRoundRobin = roundRobinMap.get(key);
            int weight = weights[i];
            if(null == weightedRoundRobin){
                weightedRoundRobin = new WeightedRoundRobin();
                weightedRoundRobin.setWeight(weight);
                roundRobinMap.putIfAbsent(key, weightedRoundRobin);
            }
            if(weight != weightedRoundRobin.getWeight()){
                weightedRoundRobin.setWeight(weight);
            }

            long cur = weightedRoundRobin.increaseCurrent();

            if(cur > maxCurrent){
                maxCurrent = cur;
                selectedClient = client;
                selectedWRR = weightedRoundRobin;
            }
            totalWeight += weight;
        }

        if(null == selectedClient){
            return clients.get(0);
        }

        selectedWRR.sel(totalWeight);

        return selectedClient;
    }
}