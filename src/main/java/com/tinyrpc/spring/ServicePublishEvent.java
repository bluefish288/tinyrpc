package com.tinyrpc.spring;

import org.springframework.context.ApplicationEvent;

public class ServicePublishEvent extends ApplicationEvent {
    public ServicePublishEvent(ServiceBean serviceBean) {
        super(serviceBean);
    }
    public ServiceBean getServiceBean(){
        return (ServiceBean) getSource();
    }
}
