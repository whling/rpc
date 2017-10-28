package com.whl.rpc.client;

import com.whl.rpc.common.RpcRequest;
import com.whl.rpc.common.RpcResponse;
import com.whl.rpc.registry.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author whling
 * @description: 客户端调用方法的代理对象，通过此类调用底层的通信框架去向远程服务获取信息返回
 * @date 2017年6月29日下午9:45:03
 */
public class RpcProxy {
    // 注册中心地址
    private String serverAddress;
    // 查找服务地址的对象
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建代理类对象，通过此对象去调用netty访问远程服务获取结果
     *
     * @param interfaceClass 服务的接口类
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        T proxy = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    /**
                     * proxy:目标对象 method:目标方法 args:目标方法参数
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建请求封装数据对象
                        RpcRequest request = new RpcRequest();
                        String serviceName = method.getDeclaringClass().getName();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(serviceName);
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameter(args);
                        // 查找服务
                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discovery(serviceName);
                        } // 随机获取服务的地址
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        // 创建RpcClient，链接服务端
                        RpcClient client = new RpcClient(host, port);
                        // 通过netty实现RPC
                        RpcResponse response = client.send(request);
                        // 返回信息
                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
        return proxy;
    }
}
