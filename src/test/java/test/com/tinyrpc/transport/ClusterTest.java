package test.com.tinyrpc.transport;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.Constants;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.registry.ServiceListener;
import com.tinyrpc.registry.URL;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.cluster.Cluster;
import com.tinyrpc.transport.cluster.ClusterFactory;
import org.hamcrest.core.IsAnything;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import test.com.tinyrpc.mock.HelloService;

public class ClusterTest {

    private Mockery mockery;

    private Discovery discovery;

    @Before
    public void init(){
        mockery = new JUnit4Mockery();
        mockery.setImposteriser(ClassImposteriser.INSTANCE);

        discovery = mockery.mock(Discovery.class);


    }

    @Test
    public void testFailfast(){
        Expectations expectations = new Expectations();


        expectations.allowing(discovery).subscribe(expectations.with(new IsAnything<URL>()), expectations.with(new IsAnything<ServiceListener>()));

        mockery.checking(expectations);


        InvokeConfig<HelloService> invokeConfig = new InvokeConfig<>();
        invokeConfig.setInterCls(HelloService.class);
        invokeConfig.setClusterType(Cluster.Type.failfast);

        Cluster cluster = ClusterFactory.INSTANCE.newCluster(invokeConfig, discovery);

        Request request = new Request();
        request.setInterfaceName(HelloService.class.getName());
        try {
            cluster.send(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }


}
