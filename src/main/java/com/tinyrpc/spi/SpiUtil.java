package com.tinyrpc.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SpiUtil {

    public static <T> T get(Class<T> cls){
        ServiceLoader<T> serviceLoader = ServiceLoader.load(cls);
        Iterator<T> it = serviceLoader.iterator();
        T obj = null;
        while (it.hasNext()){
            obj = it.next();
        }
        return obj;
    }
}