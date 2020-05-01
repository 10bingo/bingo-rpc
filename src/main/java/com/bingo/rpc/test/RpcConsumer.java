package com.bingo.rpc.test;

import com.bingo.rpc.api.Hello;
import com.bingo.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {
    public static void main(String[] args) {
        Hello rpcHello = RpcProxy.create(Hello.class);
        String bingo = rpcHello.sayHello("bingo");
        System.out.println(bingo);
    }
}
