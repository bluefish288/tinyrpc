package test.com.tinyrpc.proxy;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.proxy.ProxyFactory;
import com.tinyrpc.proxy.jdk.JdkProxyFactory;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.cluster.AbstractCluster;
import com.tinyrpc.transport.cluster.Cluster;
import org.hamcrest.core.IsAnything;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import test.com.tinyrpc.mock.HelloService;

public class JdkProxyFactoryTest {

    private ProxyFactory proxyFactory;

    private Mockery mockery;

    @Before
    public void init(){

        proxyFactory = new JdkProxyFactory();

        mockery = new JUnit4Mockery(){
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

    }


    @Test
    public void testGetProxy(){
        Cluster cluster = mockery.mock(AbstractCluster.class);

        Request request = new Request();
        request.setInterfaceName(HelloService.class.getName());

        ResponseFuture responseFuture = new ResponseFuture();
        responseFuture.setValue("testaaa");

        mockery.checking(new Expectations() {
            {
                try {
                    oneOf(cluster).send(with(new IsAnything<Request>()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RpcException e) {
                    e.printStackTrace();
                }
                will(returnValue(responseFuture));
            }
        });

        InvokeConfig<HelloService> invokeConfig = new InvokeConfig<>();
        invokeConfig.setInterCls(HelloService.class);

        HelloService helloService = proxyFactory.getProxy(invokeConfig, cluster);

        System.out.println(helloService.sayHello("test"));
    }

}
