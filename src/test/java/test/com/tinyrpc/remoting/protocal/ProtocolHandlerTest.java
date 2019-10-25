package test.com.tinyrpc.remoting.protocal;

import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.remoting.protocal.ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class ProtocolHandlerTest {


    @Test
    public void test() {

        Message message = Message.build();
        message.setBody("hello".getBytes());
        message.setMessageId(new Random().nextLong());

        System.out.println(message);

        ProtocolHandler<ByteBuf> handler = new NettyProtocolHandler();

        byte[] bytes = handler.encodeMessage(message);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(bytes.length);

        buf.writeBytes(bytes);

        Message decodedMessage = handler.decodeMessage(buf);
        System.out.println(decodedMessage);

        Assert.assertTrue(null!=decodedMessage);
        Assert.assertTrue(decodedMessage.getMessageId() == message.getMessageId());
        Assert.assertArrayEquals(decodedMessage.getBody(), message.getBody());

    }
}
