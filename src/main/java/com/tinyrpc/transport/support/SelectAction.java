package com.tinyrpc.transport.support;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface SelectAction {

    public void onAccept(SelectionKey key) throws IOException;

    public void onConnected(SelectionKey key) throws IOException;

    public void onRead(SelectionKey key) throws IOException;

    public void onWrite(SelectionKey key) throws IOException;
}
