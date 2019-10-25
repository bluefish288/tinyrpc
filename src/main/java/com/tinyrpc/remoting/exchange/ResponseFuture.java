package com.tinyrpc.remoting.exchange;


import com.tinyrpc.common.exception.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ResponseFuture {

    private final static Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

    private Object lock = new Object();

    private Object value;

    private long timeoutMilliseconds = 5000;

    private AtomicBoolean timeouted = new AtomicBoolean(true);

    public void setValue(Object value) {
        this.setValue(value, false);
    }

    public void setValue(Object value, boolean hasException) {
        this.value = value;
        synchronized (lock){
            timeouted.compareAndSet(true,false);
            lock.notify();
        }
    }

    public void setTimeout(long timeout) {
        this.timeoutMilliseconds = timeout;
    }

    public Object getValue() {

        if(null == value){
            try {
                synchronized (lock){
                    if(null!=value){
                        return value;
                    }
                    lock.wait(timeoutMilliseconds);
                    if(timeouted.get()){
                        this.value = new TimeoutException();
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }
        }

        return value;

    }

    public boolean isHasException() {
        if(null == value){
            getValue();
        }
        while (Thread.currentThread().getState() == Thread.State.TIMED_WAITING);
       return null!=value && value instanceof Throwable;
    }

    @Override
    public String toString() {
        return "ResponseFuture{" +
                "lock=" + lock +
                ", value=" + value +
                ", timeoutMilliseconds=" + timeoutMilliseconds +
                ", timeouted=" + timeouted +
                '}';
    }
}