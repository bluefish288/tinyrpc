package com.tinyrpc.transport.server;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class InvokeKey {

    private String group;

    private String serviceName;

    private String version;

    public InvokeKey(String group, String serviceName, String version) {
        assert StringUtils.isNotBlank(group);
        assert StringUtils.isNotBlank(serviceName);
        assert StringUtils.isNotBlank(version);
        this.group = group;
        this.serviceName = serviceName;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokeKey invokeKey = (InvokeKey) o;
        return Objects.equals(group, invokeKey.group) &&
                Objects.equals(serviceName, invokeKey.serviceName) &&
                Objects.equals(version, invokeKey.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, serviceName, version);
    }

    @Override
    public String toString() {
        return "InvokeKey{" +
                "group='" + group + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
