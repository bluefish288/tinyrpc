package com.tinyrpc.remoting.protocal;

import java.util.Random;

public class MessageIdGenerator {

    public static long generateMessageId(){
        return new Random().nextLong();
    }
}