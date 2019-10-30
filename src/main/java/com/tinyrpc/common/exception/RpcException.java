package com.tinyrpc.common.exception;

public class RpcException extends RuntimeException{


    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int FORBIDDEN_EXCEPTION = 4;
    public static final int SERIALIZATION_EXCEPTION = 5;
    public static final int NO_INVOKER_AVAILABLE = 6;
    public static final int LIMIT_EXCEEDED_EXCEPTION = 7;
    public static final int CONFIG_ERROR = 8;
    public static final int NO_SERVICE_EXIST = 9;

    private int code = UNKNOWN_EXCEPTION;

    public int getCode() {
        return code;
    }

    public RpcException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(int code) {
        this.code = code;
    }

    public RpcException(String message, int code) {
        super(message);
        this.code = code;
    }
}