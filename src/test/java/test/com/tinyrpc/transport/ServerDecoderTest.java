package test.com.tinyrpc.transport;

import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.codec.exchange.RequestCodec;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import test.com.tinyrpc.mock.HelloService;
import com.tinyrpc.remoting.exchange.Exchange;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.server.netty.handler.ServerDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.io.IOException;

public class ServerDecoderTest {

    private ProtocolHandler<ByteBuf> protocolHandler = new NettyProtocolHandler();

    private ExchangeCodec<Request> requestCodec = new RequestCodec();

    @Test
    public void testDecode(){
        EmbeddedChannel channel = new EmbeddedChannel(new ServerDecoder());

        Message message = Message.build();
        message.setMessageId(123456);


        Request request = new Request();
        request.setInterfaceName(HelloService.class.getName());
        request.setMethodName("sayHello");
        byte[] bytes = null;
        try {
            bytes = requestCodec.encode(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setBody(bytes);



        byte[] msgBytes = protocolHandler.encodeMessage(message);


        ByteBuf in = Unpooled.copiedBuffer(msgBytes);

        channel.writeInbound(in);


        Exchange exchange = channel.readInbound();

        System.out.println(exchange);

    }
}