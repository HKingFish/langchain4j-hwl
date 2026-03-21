package com.kingfish.langchain4j.config;

import cn.hutool.extra.spring.SpringUtil;
import dev.langchain4j.spi.classloading.ClassInstanceFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author : haowl
 * @Date : 2026/3/21 9:46
 * @Desc :
 */
@Slf4j
public class SpringClassInstanceFactory implements ClassInstanceFactory {
    @Override
    public <T> T getInstanceOfClass(Class<T> clazz) {
        return SpringUtil.getBean(clazz);
    }
}