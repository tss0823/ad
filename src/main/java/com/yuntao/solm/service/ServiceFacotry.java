package com.yuntao.solm.service;

import com.yuntao.solm.service.impl.AdQrClickServiceImpl;
import com.yuntao.solm.service.impl.AdXfClickServiceImpl;

import java.util.*;

/**
 * Created by shengshan.tang on 2015/11/22 at 19:34
 */
public class ServiceFacotry {

    private static Map<String,Object> beanMap = new HashMap<String, Object>();
    private static Map<String,Class> beanClsMap = new HashMap<String, Class>();

    static {
        beanClsMap.put("adQrClickService", AdQrClickServiceImpl.class);
        beanClsMap.put("adXfClickService", AdXfClickServiceImpl.class);
    }

    public static synchronized Object getBean(String beanName){
        Object bean = beanMap.get(beanName);
        if(bean == null){
            Class cls = beanClsMap.get(beanName);
            try {
                bean =  cls.newInstance();
                beanMap.put(beanName,bean);
            } catch (Exception e) {
                throw new RuntimeException("bean build failed!",e);
            }
        }
        return bean;
    }
}
