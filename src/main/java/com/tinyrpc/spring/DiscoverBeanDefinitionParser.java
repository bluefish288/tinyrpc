package com.tinyrpc.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class DiscoverBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return DiscoverBean.class;
    }

    @Override
    protected String getBeanClassName(Element element) {
        return DiscoverBean.class.getName();
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String connectStr = element.getAttribute("connectStr");
        builder.addPropertyValue("connectStr",connectStr);
    }
}
