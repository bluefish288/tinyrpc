package com.tinyrpc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ByteUtil {

    private final static Logger logger = LoggerFactory.getLogger(ByteUtil.class);

    public static List<Byte> toList(byte[] array) {
        if (array == null) {
            return null;
        }

        List<Byte> list = new ArrayList<Byte>(array.length);

        for (byte value : array) {
            list.add(value);
        }

        return list;
    }

    public static byte[] toArray(List<Byte> list) {
        if (list == null) {
            return null;
        }

        byte[] array = new byte[list.size()];

        int index = 0;
        for (byte value : list) {
            array[index++] = value;
        }

        return array;
    }


    /**
     * 把long类型的value转为8个byte字节，放到byte数组的off开始的位置，高位在前
     *
     * @param value
     * @param bytes
     * @param off
     */
    public static void long2bytes(long value, byte[] bytes, int off) {
        bytes[off + 7] = (byte) value;
        bytes[off + 6] = (byte) (value >>> 8);
        bytes[off + 5] = (byte) (value >>> 16);
        bytes[off + 4] = (byte) (value >>> 24);
        bytes[off + 3] = (byte) (value >>> 32);
        bytes[off + 2] = (byte) (value >>> 40);
        bytes[off + 1] = (byte) (value >>> 48);
        bytes[off] = (byte) (value >>> 56);
    }

    /**
     * 把byte数组中off开始的8个字节，转为long类型，高位在前
     *
     * @param bytes
     * @param off
     */
    public static long bytes2long(byte[] bytes, int off) {
        return ((bytes[off + 7] & 0xFFL)) + ((bytes[off + 6] & 0xFFL) << 8) + ((bytes[off + 5] & 0xFFL) << 16)
                + ((bytes[off + 4] & 0xFFL) << 24) + ((bytes[off + 3] & 0xFFL) << 32) + ((bytes[off + 2] & 0xFFL) << 40)
                + ((bytes[off + 1] & 0xFFL) << 48) + (((long) bytes[off]) << 56);
    }

    /**
     * 把int类型的value转为4个byte字节，放到byte数组的off开始的位置，高位在前
     *
     * @param value
     * @param bytes
     * @param off
     */
    public static void int2bytes(int value, byte[] bytes, int off) {
        // value 4个字节
        bytes[off + 3] = (byte) value; // byte是8bit， 所以(byte) value取了最低8bit，
        bytes[off + 2] = (byte) (value >>> 8); // 无符号右移8位，即15-8移动到了7-0,然后再取最低8bit。
        bytes[off + 1] = (byte) (value >>> 16);
        bytes[off] = (byte) (value >>> 24);
    }

    /**
     * 把byte数组中off开始的4个字节，转为int类型，高位在前
     *
     * @param bytes
     * @param off
     */
    public static int bytes2int(byte[] bytes, int off) {
        return ((bytes[off + 3] & 0xFF)) + ((bytes[off + 2] & 0xFF) << 8) + ((bytes[off + 1] & 0xFF) << 16) + ((bytes[off]) << 24);
    }

    /**
     * 把short类型的value转为2个byte字节，放到byte数组的off开始的位置，高位在前
     *
     * @param value
     * @param bytes
     * @param off
     */
    public static void short2bytes(short value, byte[] bytes, int off) {
        bytes[off + 1] = (byte) value;
        bytes[off] = (byte) (value >>> 8);
    }

    /**
     * 把byte数组中off开始的2个字节，转为short类型，高位在前
     *
     * @param b
     * @param off
     */
    public static short bytes2short(byte[] b, int off) {
        return (short) (((b[off + 1] & 0xFF)) + ((b[off] & 0xFF) << 8));
    }

    public static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            return bos.toByteArray();
        } finally {
            if (gzip != null) {
                gzip.close();
            }
        }

    }

    public static byte[] unGzip(byte[] data) throws IOException {
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buf = new byte[2048];
            int size = -1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length + 1024);
            while ((size = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, size);
            }
            return bos.toByteArray();
        } finally {
            if (gzip != null) {
                gzip.close();
            }
        }

    }

    public static byte setBit(byte b, short offset){
        assert offset >= 0 && offset <= 7;
        // 0x80 : 1000 0000
        return (byte)((b | (0x80 >>> offset)));
    }

    public static boolean isBitSet(byte b, short offset){
        assert offset >= 0 && offset <= 7;
        byte testByte = (byte) ((0x80 >>> offset) & 0xFF);
        return (b & testByte) == testByte;
    }
}
