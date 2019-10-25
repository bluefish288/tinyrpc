package com.tinyrpc.registry.zk;

import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.registry.ServiceListener;
import com.tinyrpc.registry.URL;
import com.tinyrpc.registry.Discovery;
import com.tinyrpc.transport.server.InvokeKey;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ZookeeperDiscovery extends ZookeeperSupport implements Discovery {

    private final static Logger logger = LoggerFactory.getLogger(ZookeeperDiscovery.class);


    private ConcurrentHashMap<URL, IZkChildListener> childListenerMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<URL, ServiceListener> subscribeListenerMap = new ConcurrentHashMap<>();

    private final ReentrantLock clientLock = new ReentrantLock();

    public ZookeeperDiscovery(String connectStr) {
        super(connectStr);

        zkClient().subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

            }

            @Override
            public void handleNewSession() throws Exception {

                for (Iterator<URL> iterator = subscribeListenerMap.keySet().iterator(); iterator.hasNext(); ) {
                    URL url = iterator.next();
                    subscribe(url, subscribeListenerMap.get(url));
                }

            }
        });
    }

    @Override
    public void subscribe(URL url, ServiceListener listener) {
        try {

            clientLock.lock();

            final String serviceNamePath = url.toServicePath();

            IZkChildListener zkChildListener = childListenerMap.get(url);
            if (null == zkChildListener) {
                zkChildListener = new IZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {

                        if (logger.isDebugEnabled()) {
                            logger.debug(parentPath + ":" + currentChilds);
                        }

                        if (null == parentPath || (!parentPath.equals(serviceNamePath))) {
                            return;
                        }


                        InvokeKey invokeKey = getInvokeKey(parentPath);

                        if (null == currentChilds || currentChilds.size() == 0) {
                            listener.onServiceRemove(invokeKey);
                            return;
                        }

                        List<ServiceInfo> serviceInfos = new ArrayList<>();
                        for (String childPath : currentChilds) {
                            String path = parentPath + "/" + childPath;
                            String data = readData(path);
                            ServiceInfo serviceInfo = ServiceInfo.valueOf(data);
                            serviceInfos.add(serviceInfo);
                        }

                        listener.onServiceRefresh(invokeKey, serviceInfos);
                    }
                };

                childListenerMap.putIfAbsent(url, zkChildListener);
            }

            subscribeListenerMap.putIfAbsent(url, listener);

            List<String> list = zkClient().subscribeChildChanges(serviceNamePath, zkChildListener);

            if (logger.isDebugEnabled()) {
                logger.debug(String.valueOf(list));
            }


            List<String> childList = zkClient().getChildren(serviceNamePath);
            if (null != childList && childList.size() > 0) {

                List<ServiceInfo> serviceInfos = new ArrayList<>();
                for (String childPath : childList) {
                    String path = serviceNamePath + "/" + childPath;
                    String data = readData(path);
                    ServiceInfo serviceInfo = ServiceInfo.valueOf(data);
                    serviceInfos.add(serviceInfo);
                }

                InvokeKey invokeKey = getInvokeKey(serviceNamePath);
                listener.onServiceRefresh(invokeKey, serviceInfos);

            }


        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            clientLock.unlock();
        }
    }

    @Override
    public void unsubscribe(URL url, ServiceListener listener) {
        IZkChildListener zkChildListener = childListenerMap.get(url);
        if (null != zkChildListener) {
            zkClient().unsubscribeChildChanges(url.toServicePath(), zkChildListener);
        }
        InvokeKey invokeKey = new InvokeKey(url.getGroup(), url.getServiceName(), url.getVersion());
        listener.onServiceRemove(invokeKey);
    }

    private InvokeKey getInvokeKey(String servicePath) {
        String[] arr = servicePath.split("/");

        String group = "";
        String serviceName = "";
        String version = "";
        if (arr.length >= 3) {
            group = arr[2];
        }
        if (arr.length >= 4) {
            serviceName = arr[3];
        }
        if (arr.length >= 5) {
            version = arr[4];
        }

        return new InvokeKey(group, serviceName, version);
    }
}