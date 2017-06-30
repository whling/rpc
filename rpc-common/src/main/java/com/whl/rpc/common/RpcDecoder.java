package com.whl.rpc.common;

import java.util.List;

import com.whl.rpc.common.util.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * @description: netty在接收数据时的解码器
 * @author whling
 * @date 2017年6月28日下午9:00:19
 *
 */
public class RpcDecoder extends ByteToMessageDecoder {
	private Class<?> genericClass;

	// 通过构造方法传入反序列化对象Class
	public RpcDecoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
		}
		// 将ByteBuf转换为byte[]
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		// 将data转换成object
		Object obj = SerializationUtil.deserialize(data, genericClass);
		out.add(obj);
	}

}
