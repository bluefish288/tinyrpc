package com.tinyrpc.transport.server.netty;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private Map<String,Channel> channelMap = new ConcurrentHashMap<>();

    public void add(Channel channel){
        String key = toAddressString((InetSocketAddress) channel.remoteAddress());
        channelMap.put(key, channel);
    }

    public void remove(Channel channel){
        String key = toAddressString((InetSocketAddress) channel.remoteAddress());
        channelMap.remove(key);
    }

    public Collection<Channel> channels(){
        return channelMap.values();
    }

    private static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }
}