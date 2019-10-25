package test.com.tinyrpc.transport;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.transport.server.ServiceProcessor;
import com.tinyrpc.transport.server.ServiceConfig;
import com.tinyrpc.transport.server.ServiceContext;
import org.junit.Assert;
import org.junit.Test;
import test.com.tinyrpc.mock.DemoHelloService;
import test.com.tinyrpc.mock.HelloService;

public class ServiceProcessorTest {

    @Test
    public void testProcess(){
        ServiceContext serviceContext = ServiceContext.INSTANCE;
        ServiceConfig config = new ServiceConfig();
        config.setInterCls(HelloService.class);
        config.setService(new DemoHelloService());
        try {
            serviceContext.addService(config);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        ServiceProcessor serviceProcessor = new ServiceProcessor();

        HelloService helloService = new DemoHelloService();


        Request request = new Request();
        request.setInterfaceName(HelloService.class.getName());
        request.setMethodName("sayHello");
        request.setParameterTypes(new Class[]{String.class});
        request.setArguments(new Object[]{"test in mock"});
        Object result =  serviceProcessor.getValue(request);
        Assert.assertTrue(null!=result);
        System.out.println(result);

    }


}