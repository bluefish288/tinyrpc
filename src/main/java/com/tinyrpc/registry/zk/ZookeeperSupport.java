package com.tinyrpc.registry.zk;

import com.tinyrpc.registry.URL;
import org.I0Itec.zkclient.ZkClient;

public abstract class ZookeeperSupport {

    private ZkClient zkClient;

    public ZookeeperSupport(String connectStr) {
        this.zkClient = new ZkClient(connectStr, 10000, 10000);
    }

    public ZkClient zkClient(){
        return zkClient;
    }

    protected void createData(URL url, String data) {

        if(!zkClient.exists(url.toServicePath())){
            zkClient.createPersistent(url.toServicePath(), true);
        }

        String nodePath = url.toFullPath();
        if (!zkClient.exists(nodePath)) {
            zkClient.createEphemeral(nodePath, data);
        } else {
            zkClient.writeData(nodePath, data);
        }
    }

    protected String readData(String nodePath) {
        if (zkClient.exists(nodePath)) {
            return zkClient.readData(nodePath);
        }
        return null;
    }

    protected boolean deleteData(String nodePath) {
        return zkClient.exists(nodePath) && zkClient.delete(nodePath);
    }
}