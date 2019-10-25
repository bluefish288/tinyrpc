package com.tinyrpc.registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceInfo {

    public final static int DEFAULT_WEIGHT =100;

    private String host;

    private int port;

    private int weight = DEFAULT_WEIGHT;

    public ServiceInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ServiceInfo(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getWeight() {
        return weight;
    }

    public String toHostData(){
        return host+":"+port;
    }

    public String toData(){
        return host+":"+port+"/weight="+weight;
    }

    public static ServiceInfo valueOf(String data){
        String[] arr = data.split("/");
        String[] hostInfo = arr[0].split(":");
        String host = hostInfo[0];
        int port = Integer.valueOf(hostInfo[1]);

        String params = arr[1];
        Map<String,String> paramMap = new HashMap<>();
        Arrays.asList(params.split("&")).forEach(s -> {
            String[] ss = s.split("=");
            if(ss.length == 1){
                paramMap.putIfAbsent(ss[0],"");
            }else if(ss.length == 2){
                paramMap.putIfAbsent(ss[0], ss[1]);
            }
        });

        int weight = DEFAULT_WEIGHT;
        if(paramMap.containsKey("weight")){
            String v = paramMap.get("weight");
            if(null!=v && (!v.equals(""))){
                weight = Integer.valueOf(v);
            }
        }


        return new ServiceInfo(host,port,weight);
    }
}