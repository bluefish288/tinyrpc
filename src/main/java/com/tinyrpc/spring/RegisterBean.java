package com.tinyrpc.spring;

import com.tinyrpc.transport.server.ServiceConfig;
import com.tinyrpc.transport.server.ServiceProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

public class RegisterBean implements InitializingBean, ApplicationListener<ServicePublishEvent> {

    private String connectStr;

    private ServiceProvider serviceProvider;

    public String getConnectStr() {
        return connectStr;
    }

    public void setConnectStr(String connectStr) {
        this.connectStr = connectStr;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serviceProvider = new ServiceProvider(connectStr);
    }

    @Override
    public void onApplicationEvent(ServicePublishEvent servicePublishEvent) {
        ServiceBean serviceBean = servicePublishEvent.getServiceBean();

        if(null == serviceBean){
            return;
        }

        ServiceConfig config = new ServiceConfig();
        config.setInterCls(serviceBean.getInterfaceClass());
        config.setService(serviceBean.getRefBean());
        config.setGroup(serviceBean.getGroup());
        config.setVersion(serviceBean.getVersion());

        if(null!=serviceBean.getWeight()){
            config.setWeight(serviceBean.getWeight());
        }

        serviceProvider.export(config);
    }
}
