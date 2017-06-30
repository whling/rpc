package com.whl.rpc.app_server;

import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * 
 * @description: 服务的启动类
 * @author whling
 * @date 2017年6月30日上午8:33:27
 *
 */
public class AppBoostrap {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
	}
}
