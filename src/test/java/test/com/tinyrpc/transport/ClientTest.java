package test.com.tinyrpc.transport;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.proxy.jdk.JdkProxyFactory;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.cluster.Cluster;
import org.hamcrest.core.IsAnything;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import test.com.tinyrpc.mock.HelloService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientTest {

    private Mockery mockery;

    private Cluster cluster;

    @Before
    public void init() {

        mockery = new JUnit4Mockery();
        mockery.setImposteriser(ClassImposteriser.INSTANCE);

        cluster = mockery.mock(Cluster.class);

    }

    @Test
    public void test() {

        InvokeConfig<HelloService> invokeConfig = new InvokeConfig<>();
        invokeConfig.setInterCls(HelloService.class);

        mockery.checking(new Expectations() {
            {
                try {
                    allowing(cluster).send(with(new IsAnything<Request>()));

                    ResponseFuture future = new ResponseFuture();
                    future.setValue("test");

                    will(returnValue(future));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
        });

        HelloService fooService = new JdkProxyFactory().getProxy(invokeConfig, cluster);

        List<Thread> threads = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> list = Arrays.asList("测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123",
                            "测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123测试123");

                    for (int i = 0; i < 20; i++) {
                        System.out.println("result = " + fooService.wrap(list));
                    }

                }
            });

            t.setName("client-invoke-" + i);

            t.start();

            threads.add(t);

        }

        for (Thread t : threads) {
            try {
                t.join();
                System.out.println(t.getName() + " finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}