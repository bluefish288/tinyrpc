package com.tinyrpc.remoting.protocal;

import java.util.Arrays;

public class Message {

    private final static byte DEFAULT_VERSION = 0x00;

    private byte version = DEFAULT_VERSION;

    private long messageId;

    private boolean heartbeat = false;

    private boolean hasException = false;

    private boolean zip = false;

    private int length;

    private byte[] body;


    private Message() {
    }

    public static Message build(){
        return new Message();
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public boolean isHasException() {
        return hasException;
    }

    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }

    public boolean isZip() {
        return zip;
    }

    public void setZip(boolean zip) {
        this.zip = zip;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "version=" + version +
                ", messageId=" + messageId +
                ", heartbeat=" + heartbeat +
                ", hasException=" + hasException +
                ", length=" + length +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}