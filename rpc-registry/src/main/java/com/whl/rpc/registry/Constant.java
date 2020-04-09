package com.whl.rpc.registry;

public class Constant {
	public static final int ZK_SESSION_TIMEOUT = 30000;
	/**
	 * 服务注册的基地址
	 */
	public static final String ZK_REGISTRY_PATH = "/rpc";
	public static final String ZK_REGISTRY__BASE_PATH = ZK_REGISTRY_PATH+"/servers";
	// 每个服务的节点名前缀
	public static final String ZK_REGISTRY_DATA = "/data";
}
