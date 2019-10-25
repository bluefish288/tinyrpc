package com.tinyrpc.transport.loadbalance;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.client.Client;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface LoadBalance {

    public Client select(List<Client> clients, Request request) throws RpcException;

    public static enum Type{

        roundrobin,
        random

        ;

        public static Type byName(String name){
            if(StringUtils.isBlank(name)){
                return null;
            }
            for(Type type : Type.values()){
                if(type.name().equals(name)){
                    return type;
                }
            }
            return null;
        }

        public static Type byNameWithDefault(String name){
            Type type = byName(name);
            if(null == type){
                return roundrobin;
            }
            return type;
        }


    }
}