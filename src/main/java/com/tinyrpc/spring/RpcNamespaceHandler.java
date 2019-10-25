package com.tinyrpc.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("register", new RegisterBeanDefinitionParser());
        registerBeanDefinitionParser("provider", new ServiceBeanDefinitionParser());

        registerBeanDefinitionParser("discover", new DiscoverBeanDefinitionParser());
        registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParser());
    }
}
