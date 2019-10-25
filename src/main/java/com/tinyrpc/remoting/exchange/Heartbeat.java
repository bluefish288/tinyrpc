package com.tinyrpc.remoting.exchange;

public class Heartbeat extends Exchange {

    private long sendTime = 0;

    private long receiveTime = 0;

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    @Override
    public String toString() {
        return "HeartbeatResp{" +
                "sendTime=" + sendTime +
                ", receiveTime=" + receiveTime +
                "} " + super.toString();
    }
}