package com.bingo.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvokerProtocol implements Serializable {

    //服务名，其实是类名或接口名
    private String serviceName;
    //方法名称
    private String methodName;
    //形参的class类型
    private Class<?>[] paramTypes;
    //实参列表
    private Object[] params;

}