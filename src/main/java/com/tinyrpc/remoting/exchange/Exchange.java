package com.tinyrpc.remoting.exchange;

import java.io.Serializable;

public class Exchange implements Serializable {

    private long messageId;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "messageId=" + messageId +
                '}';
    }
}