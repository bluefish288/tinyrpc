package com.tinyrpc.registry.zk;

import com.tinyrpc.registry.Registry;
import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.registry.URL;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ZookeeperRegistry extends ZookeeperSupport implements Registry {

    private final static Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private ConcurrentHashMap<URL, ServiceInfo> registeredServiceInfos = new ConcurrentHashMap();

    private final ReentrantLock serverLock = new ReentrantLock();

    public ZookeeperRegistry(String connectStr) {

        super(connectStr);
        zkClient().subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

            }

            @Override
            public void handleNewSession() throws Exception {
                for(URL url : registeredServiceInfos.keySet()){
                    ServiceInfo serviceInfo = registeredServiceInfos.get(url);
                    register(url, serviceInfo);
                }
            }
        });
    }

    @Override
    public void register(URL url, ServiceInfo serviceInfo) {
        try{
            serverLock.lock();
            deleteData(url.toFullPath());
            createData(url, serviceInfo.toData());
            registeredServiceInfos.put(url, serviceInfo);
        }catch (Throwable e){
            logger.error(e.getMessage(), e);
        }finally {
            serverLock.unlock();
        }
    }

    @Override
    public void unregister(URL url) {
        try{
            serverLock.lock();
            deleteData(url.toFullPath());
            registeredServiceInfos.remove(url);
        }catch (Throwable e){
            logger.error(e.getMessage(), e);
        }finally {
            serverLock.unlock();
        }
    }


}