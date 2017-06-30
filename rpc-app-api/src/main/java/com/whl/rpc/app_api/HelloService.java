package com.whl.rpc.app_api;

/**
 * 
 * @description: 对外提供的服务接口
 * @author whling
 * @date 2017年6月29日下午9:47:31
 *
 */
public interface HelloService {

    String hello(String name);

    String hello(Person person);
}
