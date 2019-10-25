package test.com.tinyrpc;

import com.tinyrpc.registry.zk.ZookeeperDiscovery;
import com.tinyrpc.transport.client.ServiceInvoker;
import com.tinyrpc.transport.server.ServiceConfig;
import com.tinyrpc.transport.server.ServiceProvider;
import test.com.tinyrpc.mock.DemoHelloService;
import test.com.tinyrpc.mock.HelloService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainTest {

    private final static CountDownLatch providerLatch = new CountDownLatch(1);

    private final static String connectStr = "123.57.173.142:3181,123.57.173.142:3182,123.57.173.142:3183";

    public static void main(String[] args) throws InterruptedException {
        startProvider(providerLatch);
        providerLatch.await();


        startInvoker();


    }



    private static void startProvider(CountDownLatch countDownLatch){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HelloService helloService = new DemoHelloService();

                ServiceProvider serviceProvider = new ServiceProvider(connectStr);

                for(int i=0;i<10;i++){
                    ServiceConfig config = new ServiceConfig();
                    config.setInterCls(HelloService.class);
                    config.setService(helloService);
                    config.setWeight(i+1);
                    serviceProvider.export(config);
                }
                countDownLatch.countDown();
            }
        }).start();


    }

    public static void startInvoker(){

        ServiceInvoker serviceInvoker = new ServiceInvoker(new ZookeeperDiscovery(connectStr));

        List<Thread> threads = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {

            HelloService helloService = serviceInvoker.getService(HelloService.class);
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
                        System.out.println("result = " + helloService.wrap(list));
                    }

                }
            });

            t.setName("client-invoke-"+i);

            t.start();

            threads.add(t);

        }

        for(Thread t : threads){
            try {
                t.join();
                System.out.println(t.getName()+" finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
