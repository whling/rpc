package com.whl.rpc.common;

import com.whl.rpc.common.util.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @description: netty在发送数据前的编码器
 * @author whling
 * @date 2017年6月28日下午8:54:55
 *
 */
public class RpcEncoder extends MessageToByteEncoder {
	private Class<?> genericClass;

	// 构造方法传入序列化对象的Class
	public RpcEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		if (genericClass.isInstance(msg)) {
			// 将对象序列化成byte数组
			byte[] data = SerializationUtil.serialize(msg);
			out.writeInt(data.length);
			out.writeBytes(data);
		}

	}

}
