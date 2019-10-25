package com.tinyrpc.transport.server.nio;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.Response;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import com.tinyrpc.transport.support.ByteBufferUtil;
import com.tinyrpc.transport.support.SelectAction;
import com.tinyrpc.transport.server.ServiceProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSelectAction implements SelectAction {


    private ProtocolHandler<ByteBuffer> protocolHandler;

    private ExchangeCodec<Request> requestCodec = CodecFactory.newRequestCodec();

    private ExchangeCodec<Response> responseCodec = CodecFactory.newResponseCodec();

    private ServiceProcessor serviceProcessor;

    public ServerSelectAction(ProtocolHandler<ByteBuffer> protocolHandler, ServiceProcessor serviceProcessor) {
        this.protocolHandler = protocolHandler;
        this.serviceProcessor = serviceProcessor;
    }

    @Override
    public void onAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = ssc.accept();

        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ);


    }

    @Override
    public void onConnected(SelectionKey key) throws IOException {

    }

    @Override
    public void onRead(SelectionKey key) throws IOException {

        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(8);

        buffer = ByteBufferUtil.read(channel, buffer);

        buffer.flip();

        Message message = protocolHandler.decodeMessage(buffer);

        Request request = requestCodec.decode(message.getBody());

        Response response = new Response();
        response.setMessageId(request.getMessageId());
        Object value = serviceProcessor.getValue(request);

        response.setValue(value);

        if(value instanceof Throwable){
            response.setHasException(true);
        }

        channel.register(key.selector(), SelectionKey.OP_WRITE, response);

    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {

        Response response = (Response) key.attachment();

        if(null == response){
            return;
        }

        key.attach(null);

        byte[] body = responseCodec.encode(response);

        Message message = Message.build();
        message.setMessageId(response.getMessageId());
        message.setLength(body.length);
        message.setBody(body);


        if(response.isHasException()){
            message.setHasException(true);
        }

        byte[] result = protocolHandler.encodeMessage(message);

        ByteBuffer byteBuffer = ByteBuffer.wrap(result);

        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.write(byteBuffer);

        socketChannel.register(key.selector(),SelectionKey.OP_READ);

    }
}
