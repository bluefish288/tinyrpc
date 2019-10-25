package com.tinyrpc.transport.server.nio;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.protocal.NioProtocolHandler;
import com.tinyrpc.transport.support.NioSelectionTask;
import com.tinyrpc.transport.server.AbstractServer;
import com.tinyrpc.transport.server.ServerConfig;
import com.tinyrpc.transport.support.SelectorHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NioServer extends AbstractServer {

    private final static Logger logger = LoggerFactory.getLogger(NioServer.class);

    private  ServerSocketChannel ssc;

    private SelectorHolder selectorHolder;

    private NioSelectionTask task;


    @Override
    public void start(ServerConfig serverConfig) throws RpcException {

        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ServerSocket serverSocket = ssc.socket();

            serverSocket.bind(new InetSocketAddress(serverConfig.getPort()));

            selectorHolder = new SelectorHolder().setSelector(Selector.open());

            ssc.register(selectorHolder.selector(), SelectionKey.OP_ACCEPT);

            task = new NioSelectionTask(ssc, selectorHolder, new ServerSelectAction(new NioProtocolHandler(), getServiceProcessor()));

            new Thread(task).start();

        } catch (IOException e) {
            throw new RpcException(e.getMessage(),e, RpcException.UNKNOWN_EXCEPTION);
        }

    }


    @Override
    public void close() {
        task.close();

    }
}
