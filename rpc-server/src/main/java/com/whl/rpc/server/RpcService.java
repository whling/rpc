package com.whl.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 
 * @description: rpc的请求注解（标注在服务类上）
 * @author whling
 * @date 2017年6月29日下午7:15:58
 *
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)//VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Component
public @interface RpcService {
	//服务类的接口
	Class<?> value();
}
