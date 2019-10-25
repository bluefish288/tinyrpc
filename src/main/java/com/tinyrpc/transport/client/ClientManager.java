package com.tinyrpc.transport.client;

import com.tinyrpc.registry.ServiceInfo;
import com.tinyrpc.registry.ServiceListener;
import com.tinyrpc.transport.server.InvokeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager implements ServiceListener {

    private final static Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private ConcurrentHashMap<InvokeKey, LinkedHashSet<Client>> clientsMap = new ConcurrentHashMap<>();

    public final static ClientManager INSTANCE = new ClientManager();

    @Override
    public void onServiceRefresh(InvokeKey invokeKey, List<ServiceInfo> serviceInfos) {
        if (null == invokeKey || null == serviceInfos || serviceInfos.size() == 0) {
            return;
        }

        LinkedHashSet<Client> clients = clientsMap.get(invokeKey);
        if (null == clients) {
            clientsMap.put(invokeKey, new LinkedHashSet<>());
            clients = clientsMap.get(invokeKey);
        }

        for (ServiceInfo serviceInfo : serviceInfos) {
            Client client = ClientFactory.newClient(serviceInfo.getHost(), serviceInfo.getPort());

            Iterator<Client> it = clients.iterator();
            while (it.hasNext()) {
                Client oldClient = it.next();
                if (oldClient.getRemoteHost().equals(client.getRemoteHost()) && oldClient.getRemotePort() == client.getRemotePort()) {
                    oldClient.close();
                    oldClient = null;
                    it.remove();
                }
            }

            try {
                client.connect();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }

            clients.add(client);

        }

    }

    @Override
    public void onServiceRemove(InvokeKey invokeKey) {
        if (null == invokeKey) {
            return;
        }

        clientsMap.remove(invokeKey);

    }

    public List<Client> getClients(InvokeKey invokeKey) {
        if (!clientsMap.containsKey(invokeKey)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(clientsMap.get(invokeKey));
    }


}