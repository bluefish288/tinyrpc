package test.com.tinyrpc.transport;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.client.Client;
import com.tinyrpc.transport.client.WeightManager;
import com.tinyrpc.transport.loadbalance.LoadBalance;
import com.tinyrpc.transport.loadbalance.LoadBalanceFactory;
import com.tinyrpc.transport.server.InvokeKey;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import test.com.tinyrpc.mock.HelloService;

import java.util.Arrays;
import java.util.List;

public class LoadBalanceTest extends TestCase{

    private LoadBalance loadBalance = LoadBalanceFactory.newLoadBalance(LoadBalance.Type.roundrobin);

    private Mockery mockery;


    @Override
    protected void setUp() throws Exception {
        mockery = new JUnit4Mockery(){
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
    }

    @Test
    public void testSelect(){


        Client client1 = mockery.mock(Client.class, "client1");
        mockery.checking(new Expectations(){
            {
                allowing(client1).getRemoteHost();
                will(returnValue("localhost"));
                allowing(client1).getRemotePort();
                will(returnValue(8888));
            }
        });

        Client client2 = mockery.mock(Client.class, "client2");
        mockery.checking(new Expectations(){
            {
                allowing(client2).getRemoteHost();
                will(returnValue("localhost"));
                allowing(client2).getRemotePort();
                will(returnValue(8889));
            }
        });

        Client client3 = mockery.mock(Client.class, "client3");
        mockery.checking(new Expectations(){
            {
                allowing(client3).getRemoteHost();
                will(returnValue("localhost"));
                allowing(client3).getRemotePort();
                will(returnValue(8890));
            }
        });

        List<Client> list = Arrays.asList(client1, client2, client3);

        Request request = new Request();
        request.setInterfaceName(HelloService.class.getName());

        InvokeKey invokeKey = new InvokeKey(request.getGroup(), request.getInterfaceName(),request.getVersion());
        WeightManager.INSTANCE.onServiceRefresh(invokeKey,Arrays.asList(new ServiceInfo("localhost", 8888, 100),
                new ServiceInfo("localhost", 8889, 200),
                new ServiceInfo("localhost", 8890, 300)));

        try {
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
            System.out.println(loadBalance.select(list, request));
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }
}