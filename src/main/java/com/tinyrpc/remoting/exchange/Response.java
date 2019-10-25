package com.tinyrpc.remoting.exchange;

public class Response extends Exchange{

    private Object value;

    private boolean hasException = false;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isHasException() {
        return hasException;
    }

    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }

    @Override
    public String toString() {
        return "Response{" +
                "value=" + value +
                ", hasException=" + hasException +
                "} " + super.toString();
    }
}