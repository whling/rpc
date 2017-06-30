package com.whl.rpc.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
/**
 * 
 * @description:序列化的工具类，基于protostuff实现
 * @author whling
 * @date 2017年6月28日下午7:00:26
 *
 */
public class SerializationUtil {
	// RuntimeSchema
	// 封装一个map集合存放schema对象
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

	private static Objenesis objenesis = new ObjenesisStd(true);

	// 私有化构造方法，避免外部new对象
	private SerializationUtil() {
	}

	/**
	 * 通过对象的Class来获取这个对象的schema
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T> Schema<T> getSchema(Class<T> clazz) {
		// 从map集合中获取
		Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(clazz);
			if (schema != null) {
				cachedSchema.put(clazz, schema);
			}
		}
		return schema;

	}

	/**
	 * 序列化
	 * 
	 * @param <T>
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serialize(T obj) {
		Class<T> cls = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		byte[] byteArray = null;
		try {
			Schema<T> schema = getSchema(cls);
			byteArray = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
		return byteArray;
	}

	/**
	 * 反序列化
	 * 
	 * @param <T>
	 */
	public static <T> T deserialize(byte[] data, Class<T> cls) {
		try {
			/*
			 * 如果一个类没有参数为空的构造方法时候，那么你直接调用newInstance方法试图得到一个实例对象的时候是会抛出异常的
			 * 通过ObjenesisStd可以完美的避开这个问题
			 */
			T message = (T) objenesis.newInstance(cls);
			Schema<T> schema = getSchema(cls);

			ProtostuffIOUtil.mergeFrom(data, message, schema);
			return message;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
