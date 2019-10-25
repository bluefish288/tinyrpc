package test.com.tinyrpc.registry;

import com.tinyrpc.registry.*;
import test.com.tinyrpc.mock.HelloService;
import com.tinyrpc.registry.zk.ZookeeperDiscovery;
import com.tinyrpc.registry.zk.ZookeeperRegistry;

import java.util.List;

public class ZookeeperRegistryTest {

    private final static String connectStr = "127.0.0.1:2181";

    private final static String host = "127.0.0.1";

    public static void main(String[] args){

//        test2();

    }

    private static void test1(){
        Registry registry = new ZookeeperRegistry(connectStr);
        URL url = new URL(host, 8888,HelloService.class.getName(),null,null);
        registry.register(url, new ServiceInfo(url.getHost(), url.getPort()));

        URL url1 = new URL(host, 8889,HelloService.class.getName(),null,null);
        registry.register(url1, new ServiceInfo(url1.getHost(), url1.getPort()));

        registry.unregister(url);

        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

//    private static void test2(){
//        Registry registry = new ZookeeperRegistry("127.0.0.1:2181");
//        URL url = new URL("127.0.0.1", 8888,HelloService.class.getName(),null,null);
//        registry.register(url, new ServiceInfo(url.getHost(), url.getPort()));
//
//
//        Discovery discovery = new ZookeeperDiscovery("127.0.0.1:2181");
//
//        URL discoveryUrl = new URL();
//        discoveryUrl.setServiceName(HelloService.class.getName());
//        discovery.subscribe(discoveryUrl, new ServiceListener() {
//
//            @Override
//            public void onRefreshServiceInfo(String serviceName, List<ServiceInfo> serviceInfos) {
//                System.out.println("service refresh "+ serviceInfos);
//            }
//
//            @Override
//            public void onServiceRemove(String serviceName) {
//                System.out.println("service remove for serviceName "+ serviceName);
//            }
//        });
//
//        URL url1 = new URL();
//        url1.setHost("127.0.0.1");
//        url1.setPort(8889);
//        url1.setServiceName(HelloService.class.getName());
//
//        registry.register(url1, new ServiceInfo(url1.getHost(), url1.getPort()));
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        registry.unregister(url);
//        registry.unregister(url1);
//
////        try {
////            Thread.sleep(10000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//    }
//
//    public static void test3(){
//        Discovery discovery = new ZookeeperDiscovery("127.0.0.1:2181");
//        URL url = new URL();
//        url.setHost("127.0.0.1");
//        url.setPort(8888);
//        url.setServiceName(HelloService.class.getName());
//
//        discovery.subscribe(url, new ServiceListener() {
//
//            @Override
//            public void onRefreshServiceInfo(String serviceName, List<ServiceInfo> serviceInfos) {
//                System.out.println("service refresh "+ serviceInfos);
//            }
//
//            @Override
//            public void onServiceRemove(String serviceName) {
//                System.out.println("service remove for serviceName "+ serviceName);
//            }
//        });
//
//    }

}