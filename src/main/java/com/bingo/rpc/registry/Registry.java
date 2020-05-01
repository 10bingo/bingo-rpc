package com.bingo.rpc.registry;

import com.bingo.rpc.protocol.InvokerProtocol;

import java.lang.reflect.InvocationTargetException;

public interface Registry {
    Object invoke(InvokerProtocol request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
