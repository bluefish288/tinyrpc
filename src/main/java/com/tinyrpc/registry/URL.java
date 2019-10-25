package com.tinyrpc.registry;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class URL {

    private String host;

    private Integer port;

    private String serviceName;

    private String group = Constants.DEFAULT_GROUP;

    private String version = Constants.DEFAULT_VERSION;

    private int weight = ServiceInfo.DEFAULT_WEIGHT;

    public URL(String host, Integer port, String serviceName, String group, String version, Integer weight) {
        if (StringUtils.isNotBlank(host)) {
            this.host = host;
        }

        if(null!=port){
            this.port = port;
        }

        if (StringUtils.isNotBlank(serviceName)) {
            this.serviceName = serviceName;
        }

        if (StringUtils.isNotBlank(group)) {
            this.group = group;
        }
        if (StringUtils.isNotBlank(version)) {
            this.version = version;
        }

        if (null != weight) {
            this.weight = weight;
        }

    }

    public URL(String host, Integer port, String serviceName, String group, String version) {
        this(host, port, serviceName, group, version, null);
    }

    public URL(String serviceName, String group, String version) {
        this(null,null, serviceName, group, version, null);
    }


    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    public int getWeight() {
        return weight;
    }

    public String toServicePath() {
        return Constants.NAME_SPACE + "/" + group + "/" + serviceName + "/" + version;
    }

    public String toFullPath() {
        return toServicePath() + "/" + host + ":" + port;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return  Objects.equals(port, url.port) &&
                Objects.equals(host, url.host) &&
                Objects.equals(serviceName, url.serviceName) &&
                Objects.equals(group, url.group) &&
                Objects.equals(version, url.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, serviceName, group, version);
    }

    @Override
    public String toString() {
        return "URL{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", serviceName='" + serviceName + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", weight=" + weight +
                '}';
    }
}