package com.bingo.rpc.registry;

import com.bingo.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapRegistry implements Registry {

    //保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String, Object>();

    private List<String> classNames = new ArrayList<String>();

    public MapRegistry(){
        scannerClass("com.bingo.rpc.provider");
        doRegister();
    }

    /**
     * 扫描provider所在包的所有class
     * @param packageName
     */
    private void scannerClass(String packageName){
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.","/"));
        assert url != null;
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()){
            //如果是一个文件夹继续递归
            if (file.isDirectory()){
                scannerClass(packageName+"."+file.getName());
            }else{
                classNames.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }
    }

    /**
     * 服务注册，key为接口名，value为接口实现类对象
     */
    private void doRegister(){
        if (classNames.size() == 0){
            return;
        }
        for(String className : classNames){
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(),clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object invoke(InvokerProtocol request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 在本地注册的服务Map中查找是否存在 请求的服务接口名称
        if (registryMap.containsKey(request.getServiceName())){
            Object obj = registryMap.get(request.getServiceName());

            // 从request中取出形参class类型，通过反射我们能够拿到Method对象
            Method method = obj.getClass()
                    .getMethod(request.getMethodName(),
                            request.getParamTypes());
            // 从request中取出方法入参，通过反射调用方法，得到结果
            // 通过Netty NIO写回给客户端
            Object result = method.invoke(obj,request.getParams());
            return result;
        }
        return null;
    }
}
