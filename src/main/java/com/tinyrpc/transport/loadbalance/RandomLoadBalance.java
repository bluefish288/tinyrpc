package com.tinyrpc.transport.loadbalance;

import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.client.Client;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected Client doSelect(List<Client> clients, int[] weights, Request request) {

        int length = clients.size();

        boolean sameWeight = true;

        int totalWeight = 0;

        for(int i=0;i<length;i++){
            totalWeight += weights[i];
            if(sameWeight && i > 0 && weights[i]!=weights[i-1]){
                sameWeight = false;
            }
        }

        // 权重不相同
        if(totalWeight > 0 && !sameWeight){
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            for(int i=0;i<length;i++){
                offset -= weights[i];
                if(offset < 0){
                    return clients.get(i);
                }
            }
        }

        return clients.get(ThreadLocalRandom.current().nextInt(clients.size()));
    }
}
