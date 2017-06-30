package com.whl.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whl.rpc.common.RpcDecoder;
import com.whl.rpc.common.RpcEncoder;
import com.whl.rpc.common.RpcRequest;
import com.whl.rpc.common.RpcResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @description: rpc客户端，用户发送调用服务请求、并接受请求的响应结果
 * @author whling
 * @date 2017年6月29日下午8:17:09
 *
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
	private String host;
	private int port;
	private RpcResponse response;

	private final Object obj = new Object();

	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 连接服务端，发送消息,同步调用方法
	 * 
	 * @param request 封装好的请求对象
	 * @return
	 * @throws Exception
	 */
	public RpcResponse send(RpcRequest request) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					// 向pipeline中添加编码、解码、业务处理的handler
					/**
					 * 向服务器发送远程调用，先OUT(RpcEncoder) 接收服务器响应信息，用IN（RpcDecoder、RpcClient接收）
					 */
					channel.pipeline().addLast(new RpcEncoder(RpcRequest.class)) // out
							.addLast(new RpcDecoder(RpcResponse.class)) // in
							.addLast(RpcClient.this);
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			// 链接服务器
			ChannelFuture future = bootstrap.connect(host, port).sync();
			/**
			 * 发送数据，发送数据前先进行编码RpcEncoder
			 */
			future.channel().writeAndFlush(request).sync();

			// 用线程等待的方式决定是否关闭连接
			synchronized (obj) {
				obj.wait();  //wait方法会释放锁资源，客户端一次调用没事，但是并发情况下可能会有问题
							//TODO 待解决
			}
			if (response != null) {
				future.channel().closeFuture().sync();
			}
			return response;
		} finally {
			group.shutdownGracefully();
		}
	}

	/**
	 * 接收服务提供者的响应信息
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		this.response = response;

		synchronized (obj) {
			obj.notifyAll();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("client caught exception", cause);
		ctx.close();
	}

}
