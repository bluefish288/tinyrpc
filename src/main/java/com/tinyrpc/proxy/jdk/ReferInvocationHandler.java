package com.tinyrpc.proxy.jdk;

import com.tinyrpc.common.exception.RpcException;
import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.remoting.exchange.ResponseFuture;
import com.tinyrpc.transport.client.InvokeConfig;
import com.tinyrpc.transport.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReferInvocationHandler implements InvocationHandler {

    private final static Logger logger = LoggerFactory.getLogger(ReferInvocationHandler.class);

    private InvokeConfig invokeConfig;

    private Cluster cluster;

    public ReferInvocationHandler(InvokeConfig invokeConfig, Cluster cluster) {
        this.invokeConfig = invokeConfig;
        this.cluster = cluster;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();

        Class<?> clz = invokeConfig.getInterCls();

        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return clz.getName() + "." + methodName;
        }

        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return System.identityHashCode(proxy);
        }

        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return clz.equals(args[0]);
        }

        Request request = new Request();
        request.setGroup(invokeConfig.getGroup());
        request.setInterfaceName(clz.getName());
        request.setMethodName(method.getName());

        request.setParameterTypes(parameterTypes);

        request.setArguments(args);

        request.setVersion(invokeConfig.getVersion());

        ResponseFuture responseFuture = null;
        try {
            responseFuture = cluster.send(request);
        } catch (RpcException e) {
            logger.error(e.getMessage(), e);
            return null;
        }


        Object value = responseFuture.getValue();
        if (responseFuture.isHasException() && value instanceof Throwable) {
            throw (Throwable) value;
        }

        return value;

    }
}