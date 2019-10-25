package com.tinyrpc.transport.client;

public abstract class AbstractClient implements Client {

    private String remoteHost;

    private int remotePort;

    protected ResponseHandler responseHandler = new DefaultResponseHandler();

    public AbstractClient(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractClient that = (AbstractClient) o;

        if (remotePort != that.remotePort)
            return false;
        return !(remoteHost != null ? !remoteHost.equals(that.remoteHost) : that.remoteHost != null);

    }

    @Override
    public int hashCode() {
        int result = remoteHost != null ? remoteHost.hashCode() : 0;
        result = 31 * result + remotePort;
        return result;
    }
}
