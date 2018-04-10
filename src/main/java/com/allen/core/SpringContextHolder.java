package com.allen.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringContextHolder.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) throws BeansException {
        return (T) beanFactory.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clz) throws BeansException {
        return beanFactory.getBean(clz);
    }

    public static boolean containsBean(String beanName) {
        return beanFactory.containsBean(beanName);
    }

    public static boolean isSingleton(String beanName) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(beanName);
    }

}
