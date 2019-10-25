package com.tinyrpc.spring;

import com.tinyrpc.common.util.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.context.ApplicationEventPublisher;
import org.w3c.dom.Element;

public class ServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser  {

    private final static Logger logger = LoggerFactory.getLogger(ServiceBeanDefinitionParser.class);

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ServiceBean.class;
    }

    @Override
    protected String getBeanClassName(Element element) {
        return ServiceBean.class.getName();
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String interfaceName = element.getAttribute("interface");
        try {
            builder.addPropertyValue("interfaceClass", ClassUtils.forName(interfaceName));
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
        String refBeanName = element.getAttribute("ref");
        builder.addPropertyReference("refBean",refBeanName);

        String group = element.getAttribute("group");
        if(StringUtils.isNotBlank(group)){
            builder.addPropertyValue("group",group);
        }

        String version = element.getAttribute("version");
        if(StringUtils.isNotBlank(version)){
            builder.addPropertyValue("version",version);
        }

        String weight = element.getAttribute("weight");
        if(StringUtils.isNotBlank(weight)){
            weight = weight.trim();
            if(StringUtils.isNumeric(weight)){
                builder.addPropertyValue("weight",Integer.valueOf(weight));
            }
        }

    }
}
