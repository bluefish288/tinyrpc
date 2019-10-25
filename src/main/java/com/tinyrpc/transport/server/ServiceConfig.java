package com.tinyrpc.transport.server;

import com.tinyrpc.registry.Constants;
import com.tinyrpc.registry.ServiceInfo;

public class ServiceConfig {

    private String group = Constants.DEFAULT_GROUP;

    private Class<?> interCls;

    private Object service;

    private int port = 0;

    private String version = Constants.DEFAULT_VERSION;

    private int weight = ServiceInfo.DEFAULT_WEIGHT;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getInterCls() {
        return interCls;
    }

    public void setInterCls(Class<?> interCls) {
        this.interCls = interCls;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "group='" + group + '\'' +
                ", interCls=" + interCls +
                ", service=" + service +
                ", port=" + port +
                ", version='" + version + '\'' +
                ", weight=" + weight +
                '}';
    }
}
