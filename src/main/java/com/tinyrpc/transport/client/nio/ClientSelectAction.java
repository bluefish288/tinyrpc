package com.tinyrpc.transport.client.nio;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import com.tinyrpc.transport.support.ByteBufferUtil;
import com.tinyrpc.transport.support.SelectAction;
import com.tinyrpc.transport.client.ResponseHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientSelectAction implements SelectAction {

    private ProtocolHandler<ByteBuffer> protocolHandler;

    private ResponseHandler responseHandler;

    public ClientSelectAction(ProtocolHandler<ByteBuffer> protocolHandler, ResponseHandler responseHandler) {
        this.protocolHandler = protocolHandler;
        this.responseHandler = responseHandler;
    }

    @Override
    public void onAccept(SelectionKey key) throws IOException {

    }

    @Override
    public void onConnected(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();

        if(client.isConnectionPending()){
            client.finishConnect();
        }
    }

    @Override
    public void onRead(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer receiveBuffer = ByteBuffer.allocate(8);
        receiveBuffer = ByteBufferUtil.read(client, receiveBuffer);

        receiveBuffer.flip();


        Message message = protocolHandler.decodeMessage(receiveBuffer);


        ExchangeCodec<Response> responseCodec = CodecFactory.newResponseCodec();
        Response response = responseCodec.decode(message.getBody());

        responseHandler.handle(response);
    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {
        ByteBuffer sendBuffer = (ByteBuffer) key.attachment();
        if(null == sendBuffer || sendBuffer.remaining() == 0){
           return;
        }
        key.attach(null);
        SocketChannel client = (SocketChannel) key.channel();

        client.write(sendBuffer);

        Selector selector = key.selector();

        client.register(selector, SelectionKey.OP_READ);
    }
}
