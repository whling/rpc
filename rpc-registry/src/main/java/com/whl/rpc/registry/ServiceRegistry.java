package com.whl.rpc.registry;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @description: 服务注册 ，ZK 在该架构中扮演了“服务注册表”的角色，用于注册所有服务器的地址与端口，并对客户端提供服务发现的功能
 * @author whling
 * @date 2017年6月29日上午9:55:10
 *
 */
public class ServiceRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
	// 注册中心地址
	private String registryAddress;

	public ServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	/**
	 * 向zookeeper上注册服务信息
	 * @param serviceName 服务名称
	 * @param data 服务地址
	 */
	public void registry(String serviceName, String data) {
		if (data != null) {
			ZooKeeper zk = ZKUtil.connectServer(registryAddress);
			if (zk != null) {
				// 向zookeeper注册服务信息
				createNode(zk, serviceName, data);
			}
		}
	}

	private void createNode(ZooKeeper zk, String serviceName, String data) {
		try {
			byte[] bytes = data.getBytes();
			if (zk.exists(Constant.ZK_REGISTRY_PATH, null) == null) {
				// 注册基地址不存在，则创建
				zk.create(Constant.ZK_REGISTRY_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			if (zk.exists(Constant.ZK_REGISTRY__BASE_PATH, null) == null) {
				// 注册基地址不存在，则创建
				zk.create(Constant.ZK_REGISTRY__BASE_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			if (zk.exists(Constant.ZK_REGISTRY__BASE_PATH + "/" + serviceName, null) == null) {
				// 此类服务从未注册过
				// 此节点必须是持久化类型的，否则子节点的具体信息存放不了
				zk.create(Constant.ZK_REGISTRY__BASE_PATH + "/" + serviceName, null, Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}

			String path = zk.create(Constant.ZK_REGISTRY__BASE_PATH + "/" + serviceName + Constant.ZK_REGISTRY_DATA,
					bytes, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			LOGGER.info("create zookeeper node ({} => {})", path, data);
		} catch (Exception e) {
			LOGGER.error("", e);
		}

	}

}
