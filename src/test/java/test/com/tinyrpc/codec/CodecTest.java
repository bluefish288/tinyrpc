package test.com.tinyrpc.codec;

import com.tinyrpc.codec.CodecFactory;
import com.tinyrpc.codec.exchange.ExchangeCodec;
import com.tinyrpc.remoting.exchange.Request;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CodecTest {

    @Test
    public void testCodec(){

        ExchangeCodec<Request> codec = CodecFactory.newRequestCodec();

        Request request = new Request();

        request.setInterfaceName("com.tinyrpc.test.FooService");
        request.setMethodName("sayHello");
        request.setArguments(new Object[]{"hello"});


        try {
            byte[] bytes = codec.encode(request);

            Assert.assertTrue(null!=bytes && bytes.length > 0);

            Request req1 = codec.decode(bytes);
            Assert.assertTrue(req1.getInterfaceName().equals(request.getInterfaceName()));
            Assert.assertTrue(req1.getMethodName().equals(request.getMethodName()));
            Assert.assertTrue(req1.getArguments().length == request.getArguments().length);

            for(int i=0;i<req1.getArguments().length;i++){
                Assert.assertTrue(req1.getArguments()[i].equals(request.getArguments()[i]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}