package com.whl.rpc.app_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whl.rpc.app_api.HelloService;
import com.whl.rpc.app_api.Person;
import com.whl.rpc.server.RpcService;

/**
 * 
 * @description: 服务的具体实现类
 * @author whling
 * @date 2017年6月29日下午9:53:29
 *
 */
@RpcService(value = HelloService.class)
public class HelloServiceImpl implements HelloService {
	private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

	@Override
	public String hello(String name) {
		logger.info("method hello had invoke.....");
		return name;
	}

	@Override
	public String hello(Person person) {
		logger.info("server has revice message:{}", person);
		return person.toString();
	}

}
