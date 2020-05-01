package com.bingo.rpc.provider;

import com.bingo.rpc.api.Hello;

public class HelloBingo implements Hello {

    public String sayHello(String name) {
        return "hello " + name;
    }
}
