package com.tinyrpc.transport.support;

import java.nio.channels.Selector;

public class SelectorHolder {

    private Selector selector;

    public Selector selector(){
        return selector;
    }

    public SelectorHolder setSelector(Selector selector){
        synchronized (this){
            this.selector = selector;
        }
        return this;
    }
}
