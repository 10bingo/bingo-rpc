package com.bingo.rpc.test;

import com.bingo.rpc.server.RpcNettyServer;

public class RpcServerBootstrap {

    public static void main(String[] args) {
        //启动
        RpcNettyServer rpcNettyServer = new RpcNettyServer(8080);
        rpcNettyServer.start();
    }
}
