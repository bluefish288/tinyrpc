package com.tinyrpc.transport.server;

public abstract class AbstractServer implements Server{

    private ServiceProcessor serviceProcessor;

    @Override
    public Server setRequestProcessor(ServiceProcessor serviceProcessor) {
        this.serviceProcessor = serviceProcessor;
        return this;
    }

    protected ServiceProcessor getServiceProcessor(){
        if(null == serviceProcessor){
            serviceProcessor = new ServiceProcessor();
        }
        return serviceProcessor;
    }
}
