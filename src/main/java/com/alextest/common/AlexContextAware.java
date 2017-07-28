package com.alextest.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by zhaojw30 on 2016/8/8.
 */
@Component
public class AlexContextAware implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static Object getBeanFromApplicationContext(String beanName){
        return applicationContext.getBean(beanName);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
