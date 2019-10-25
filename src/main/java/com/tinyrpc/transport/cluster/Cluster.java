package com.tinyrpc.transport.cluster;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import org.apache.commons.lang3.StringUtils;

public interface Cluster {

    public ResponseFuture send(Request request) throws InterruptedException, RpcException;

    public void close();

    public static enum Type{

        failfast,

        failover

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
                return failfast;
            }
            return type;
        }
    }
}
