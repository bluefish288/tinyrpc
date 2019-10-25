package com.tinyrpc.spring;

import com.tinyrpc.common.util.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ReferenceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private final static Logger logger = LoggerFactory.getLogger(ReferenceBeanDefinitionParser.class);

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ReferenceBean.class;
    }

    @Override
    protected String getBeanClassName(Element element) {
        return ReferenceBean.class.getName();
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String interfaceName = element.getAttribute("interface");
        String cluster = element.getAttribute("cluster");
        String loadbalance = element.getAttribute("loadbalance");
        String retries = element.getAttribute("retries");
        if(null!=retries){
            retries = retries.trim();
        }
        String group = element.getAttribute("group");
        String version = element.getAttribute("version");

        try {
            builder.addPropertyValue("interfaceClass", ClassUtils.forName(interfaceName));
            builder.addPropertyValue("cluster", cluster);
            builder.addPropertyValue("loadbalance", loadbalance);
            if(StringUtils.isNumeric(retries)){
                builder.addPropertyValue("retries",Integer.valueOf(retries));
            }
            if(StringUtils.isNotBlank(group)){
                builder.addPropertyValue("group",group);
            }
            if(StringUtils.isNotBlank(version)){
                builder.addPropertyValue("version",version);
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
    }
}
