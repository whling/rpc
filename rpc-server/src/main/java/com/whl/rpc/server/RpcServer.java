package com.whl.rpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.whl.rpc.common.RpcDecoder;
import com.whl.rpc.common.RpcEncoder;
import com.whl.rpc.common.RpcRequest;
import com.whl.rpc.common.RpcResponse;
import com.whl.rpc.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 
 * @description: rpc服务端（使用netty发布服务）
 * @author whling
 * @date 2017年6月29日下午4:12:56
 *
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private Map<String, Object> handlerMap = new HashMap<>();
	// 服务的注册类
	private ServiceRegistry serviceRegistry;
	// 服务的地址
	private String serverAddress;

	public RpcServer(ServiceRegistry serviceRegistry, String serverAddress) {
		this.serviceRegistry = serviceRegistry;
		this.serverAddress = serverAddress;
	}

	/**
	 * 获取spring容器中的自定义注解标注的服务类 执行时机：当类装配构造方法之后执行
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		/**
		 * 把所有标注了RpcService注解的类都作为服务加载进HandlerMap中
		 */
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, serviceBean);
			}
		}
	}

	/**
	 * 使用netty开启服务
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline().addLast(new RpcDecoder(RpcRequest.class))// 注册解码IN
									.addLast(new RpcEncoder(RpcResponse.class))// 注册编码OUT
									.addLast(new RpcHandler(handlerMap));// 注册RpcHandler IN
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);

			ChannelFuture future = bootstrap.bind(host, port).sync();
			LOGGER.debug("server started on port {}", port);
			// 注册本系统的所有服务
			Set<String> keySet = handlerMap.keySet();
			for (String serviceName : keySet) {
				serviceRegistry.registry(serviceName, serverAddress);
			}

			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
