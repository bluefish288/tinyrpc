package com.tinyrpc.codec.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.tinyrpc.codec.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessian2Serialization implements Serialization {
    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Hessian2Output hOut = new Hessian2Output(out);
        hOut.writeObject(obj);
        hOut.flush();
        byte[] bytes = out.toByteArray();  // 获取序列化的内容
        hOut.close();

        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
        return (T) input.readObject(clz);
    }
}