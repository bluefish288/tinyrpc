package test.com.tinyrpc.transport;

import com.tinyrpc.codec.serialize.Hessian2Serialization;
import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.Constants;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.remoting.protocal.Message;
import com.tinyrpc.remoting.protocal.NettyProtocolHandler;
import com.tinyrpc.transport.client.Client;
import com.tinyrpc.transport.client.nio.NioClient;
import com.tinyrpc.transport.server.ServiceProcessor;
import com.tinyrpc.transport.server.ServerConfig;
import com.tinyrpc.transport.server.ServiceConfig;
import com.tinyrpc.transport.server.ServiceContext;
import com.tinyrpc.transport.server.nio.NioServer;
import org.junit.Test;
import test.com.tinyrpc.mock.DemoHelloService;
import test.com.tinyrpc.mock.HelloService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class NioServerTest {


    @Test
    public void test() {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {

                ServiceConfig serviceConfig = new ServiceConfig();
                serviceConfig.setInterCls(HelloService.class);
                serviceConfig.setService(new DemoHelloService());
                try {
                    ServiceContext.INSTANCE.addService(serviceConfig);
                } catch (RpcException e) {
                    e.printStackTrace();
                }

                NioServer server = new NioServer();
                ServerConfig config = new ServerConfig();
                config.setPort(8888);
                server.setRequestProcessor(new ServiceProcessor());
                try {
                    server.start(config);
                } catch (RpcException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clientRequest();


        testClient();

    }


    private void clientRequest() {

        InetSocketAddress servAddr = new InetSocketAddress(8888);
        try {
            SocketChannel channel = SocketChannel.open(servAddr);

            Request request = new Request();
            request.setGroup(Constants.DEFAULT_GROUP);
            request.setVersion(Constants.DEFAULT_VERSION);
            request.setInterfaceName("com.test.HelloService");

            Message message = Message.build();
            message.setMessageId(123456);
            message.setHeartbeat(false);
            message.setZip(false);
            message.setBody(new Hessian2Serialization().serialize(request));


            byte[] bytes = new NettyProtocolHandler().encodeMessage(message);

            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            System.out.println(channel.write(buffer));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testClient() {
        Client client = new NioClient("127.0.0.1", 8888);
        try {
            client.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10; i++) {
            Request request = new Request();
            request.setGroup(Constants.DEFAULT_GROUP);
            request.setVersion(Constants.DEFAULT_VERSION);
            request.setInterfaceName("test.com.tinyrpc.mock.HelloService");
            request.setMethodName("sayHello");
            request.setParameterTypes(new Class<?>[]{String.class});
            request.setArguments(new Object[]{"测试"});
            try {
                ResponseFuture responseFuture = client.send(request);
                System.out.println(responseFuture.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
