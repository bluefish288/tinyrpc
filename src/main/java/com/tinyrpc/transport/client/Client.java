package com.tinyrpc.transport.client;

import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;

public interface Client {

    public String getRemoteHost();

    public int getRemotePort();

    public void connect() throws InterruptedException;

    public boolean isActive();

    public void setActive(boolean active);

    public ResponseFuture send(Request request) throws InterruptedException;

    public void close();

}