package com.whl.rpc.app_client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.whl.rpc.app_api.HelloService;
import com.whl.rpc.app_api.Person;
import com.whl.rpc.client.RpcProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {
	@Autowired
	private RpcProxy rpcProxy;

	@Test
	public void testHello1() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		String string = helloService.hello("whling");
		System.out.println(string);
	}
	@Test
	public void testHello2() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		String string = helloService.hello("casablanca");
		System.out.println(string);
	}

	@Test
	public void testHelloPerson1() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		Person person = new Person();
		person.setFirstName("whling");
		person.setLastName("casablanca");
		String string = helloService.hello(person);
		System.out.println(string);
	}
	@Test
	public void testHelloPerson2() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		Person person = new Person();
		person.setFirstName("whling");
		person.setLastName("casablanca");
		String string = helloService.hello(person);
		System.out.println(string);
	}
}
