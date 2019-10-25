package com.tinyrpc.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class ServiceBean implements InitializingBean, ApplicationEventPublisherAware {

    private Class interfaceClass;

    private Object refBean;

    private String group;

    private String version;

    private Integer weight;

    private ApplicationEventPublisher applicationEventPublisher;


    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Object getRefBean() {
        return refBean;
    }

    public void setRefBean(Object refBean) {
        this.refBean = refBean;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.applicationEventPublisher.publishEvent(new ServicePublishEvent(this));
    }
}
