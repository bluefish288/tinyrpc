package com.tinyrpc.transport.server;

import com.tinyrpc.remoting.exchange.Request;
import com.tinyrpc.common.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServiceProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ServiceProcessor.class);

    private static final int DEFAULT_CORE_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    private ServiceContext serviceContext = ServiceContext.INSTANCE;

    public ServiceProcessor() {
    }

    private ExecutorService executor = new ThreadPoolExecutor(DEFAULT_CORE_THREADS, DEFAULT_CORE_THREADS, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new RequestProcessThreadFactory());

    public Object getValue(Request request){

        Object service = null;

        try {
            service = serviceContext.getService(request);
        }catch (Throwable throwable){
            return wrapThrowable(null, throwable);
        }

        final Method method = getMethod(service.getClass(),request.getMethodName(), request.getParameterTypes());

        if(null == method){
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("No Method : ").append(request.getMethodName())
                    .append("(");
            if(null!=request.getParameterTypes()){
                messageBuilder.append(Arrays.stream(request.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(",")));
            }

            messageBuilder.append(")");

            String message = messageBuilder.toString();

            logger.error(message);

            return wrapThrowable(null, new NoSuchMethodException(message));
        }

        return execute(service, method, request.getArguments());
    }

    private Object execute(final Object service, final Method method, final Object[] args){
        Future<Object> future = executor.submit(() -> {
            MDC.put("THREAD_NAME",Thread.currentThread().getName());
            logger.info("request-call");
            return method.invoke(service, args);
        });

        Object value = null;
        try {
            value = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            value = wrapThrowable(method, e);
        }

        logger.info("value is "+value);

        return value;
    }

    private Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes){
        try {
            return cls.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private Throwable wrapThrowable(Method method, Throwable e){

        String exceptionClsName = e.getClass().getName();

        // jdk exception
        if (exceptionClsName.startsWith("java.") || exceptionClsName.startsWith("javax.")) {
            if(e instanceof InvocationTargetException){
                return ((InvocationTargetException) e).getTargetException();
            }
            return e;
        }

        // method exception in the signature
        if(null!=method){
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            for (Class<?> exceptionClass : exceptionTypes) {
                if (exceptionClsName.equals(exceptionClass.getName())) {
                    return e;
                }
            }
        }

        if(e instanceof RpcException){
            return e;
        }

        RpcException rpcException = new RpcException(e.getMessage(), e, RpcException.BIZ_EXCEPTION);
        rpcException.setStackTrace(e.getStackTrace());
        return rpcException;
    }

}