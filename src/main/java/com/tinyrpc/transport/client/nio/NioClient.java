package com.tinyrpc.transport.client.nio;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.MessageIdGenerator;
import com.tinyrpc.remoting.protocal.NioProtocolHandler;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import com.tinyrpc.transport.support.NioSelectionTask;
import com.tinyrpc.transport.support.SelectAction;
import com.tinyrpc.transport.client.AbstractClient;
import com.tinyrpc.transport.support.SelectorHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class NioClient extends AbstractClient {

    private final static Logger logger = LoggerFactory.getLogger(NioClient.class);

    private SocketChannel socketChannel;

    private SelectorHolder selectorHolder;

    private ProtocolHandler<ByteBuffer> protocolHandler = new NioProtocolHandler();

    private AtomicBoolean connected = new AtomicBoolean(false);

    public NioClient(String remoteHost, int remotePort) {
        super(remoteHost, remotePort);
    }

    @Override
    public void connect() throws InterruptedException {
        try {
            socketChannel = SocketChannel.open();
            // 设置为非阻塞方式
            socketChannel.configureBlocking(false);
            // 打开选择器
            selectorHolder = new SelectorHolder().setSelector(Selector.open());
            // 注册连接服务端socket动作
            socketChannel.register(selectorHolder.selector(), SelectionKey.OP_CONNECT);
            // 连接
            InetSocketAddress inetSocketAddress = new InetSocketAddress(getRemoteHost(), getRemotePort());
            socketChannel.connect(inetSocketAddress);
            // 分配缓冲区大小内存


            SelectAction selectAction = new ClientSelectAction(protocolHandler, responseHandler);

            NioSelectionTask task = new NioSelectionTask(socketChannel, selectorHolder, selectAction);

            new Thread(task).start();

            connected.compareAndSet(false,true);

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

    }

    @Override
    public ResponseFuture send(Request request) throws InterruptedException {

        if(!connected.get()){
            throw new RuntimeException("not connected");
        }

        request.setMessageId(MessageIdGenerator.generateMessageId());
        Message message = Message.build();


        message.setMessageId(request.getMessageId());

        ExchangeCodec<Request> requestCodec = CodecFactory.newRequestCodec();

        byte[] body = new byte[0];
        try {
            body = requestCodec.encode(request);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        message.setBody(body);

        byte[] bytes = protocolHandler.encodeMessage(message);

        ByteBuffer sendBuffer = ByteBuffer.wrap(bytes);

        try {


            socketChannel.register(selectorHolder.selector(), SelectionKey.OP_WRITE, sendBuffer);

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        ResponseFuture future = new ResponseFuture();

        responseHandler.put(request, future);

        return future;
    }

    @Override
    public void close() {

    }
}
