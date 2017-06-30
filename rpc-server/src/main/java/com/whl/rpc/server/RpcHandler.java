package com.whl.rpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.collections4.functors.ForClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whl.rpc.common.RpcRequest;
import com.whl.rpc.common.RpcResponse;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @description: rpc服务端获得请求，具体处理
 * @author whling
 * @date 2017年6月29日下午8:03:33
 *
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);
	private final Map<String, Object> handlerMap;

	RpcHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
		RpcResponse response = new RpcResponse();
		response.setRequestId(request.getRequestId());
		try {
			// 调用服务将结果返回
			Object result = handle(request);
			response.setResult(result);
		} catch (Throwable t) {
			// 表示调用失败,将失败信息返回
			LOGGER.error("调用失败：{}", t);
			response.setError(t);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private Object handle(RpcRequest request) throws Throwable {
		// 通过服务名称（服务类的接口名称）获取服务类
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);

		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameter = request.getParameter();

		Class<?> forName = Class.forName(className);
		Method method = forName.getMethod(methodName, parameterTypes);
		// 通过反射调用服务类的目标方法，并将返回值返回
		Object object = method.invoke(serviceBean, parameter);
		return object;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LOGGER.error("server caught exception", cause);
		ctx.close();
	}
}
