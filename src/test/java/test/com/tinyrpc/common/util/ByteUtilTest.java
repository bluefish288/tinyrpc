package test.com.tinyrpc.common.util;

import com.tinyrpc.codec.serialize.Hessian2Serialization;
import com.tinyrpc.common.util.ByteUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ByteUtilTest {

    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[4];
        ByteUtil.int2bytes(1,bytes,0);

        int i = 12323;
        System.out.println(Integer.toBinaryString(i));
        System.out.println(((byte)i));
        byte b = (byte) 0x80;

        System.out.println(Integer.toBinaryString(b & 0xFF));

        System.out.println(Integer.toBinaryString(ByteUtil.setBit(b, (short) 2) & 0xFF));

        b = 0x10;
        System.out.println(Integer.toBinaryString(b));
        System.out.println(ByteUtil.isBitSet(b, (short) 3));

        List<String> list = Arrays.asList("测试1","测试2","测试3","测试1","测试2","测试3");

        System.out.println(list.size());

        bytes = new Hessian2Serialization().serialize(list);
        System.out.println(bytes.length);

        byte[] zipedBytes = ByteUtil.gzip(bytes);

        System.out.println(zipedBytes.length);

        byte[] unzipedBytes = ByteUtil.unGzip(zipedBytes);

        System.out.println(unzipedBytes.length);

        System.out.println(new Hessian2Serialization().deserialize(unzipedBytes, List.class).size());

    }
}
