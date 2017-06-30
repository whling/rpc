package com.whl.rpc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @description: 服务的发现
 * @author whling
 * @date 2017年6月29日下午2:18:41
 *
 */
public class ServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);
	// 注册中心地址
	private String registryAddress;
	/**
	 * 获取所有的服务，保证可见性 <服务名称,服务地址列表>
	 */
	private volatile Map<String, List<String>> servicesMap = new ConcurrentHashMap<String, List<String>>();

	public ServiceDiscovery(String registryAddress) {
		this.registryAddress = registryAddress;
		/**
		 * 初始化服务发现类的时候就要将所有服务加载到本地
		 */
		ZooKeeper zk = ZKUtil.connectServer(registryAddress);
		if (zk != null) {
			watchService(zk);
		}
	}

	/**
	 * 获取服务的地址，简单模拟负载均衡
	 * 
	 * @param serviceName
	 *            服务名称
	 */
	public String discovery(String serviceName) {
		String data = null;
		if (serviceName != null) {
			// discover
			List<String> dataList = servicesMap.get(serviceName);
			int size = dataList.size();
			//
			if (size > 0) {
				if (size == 1) {
					data = dataList.get(0);
					LOGGER.info("using only data: {}", data);
				} else {
					data = dataList.get(ThreadLocalRandom.current().nextInt(size));
					LOGGER.info("using random data: {}", data);
				}
			}
		}
		return data;
	}

	/**
	 * 将注册中心上的所有服务全部加载到本地
	 * 
	 * @param zk
	 */
	private void watchService(final ZooKeeper zk) {
		try {
			// 获取所有子节点
			List<String> servicesList = zk.getChildren(Constant.ZK_REGISTRY__BASE_PATH, new Watcher() {
				public void process(WatchedEvent event) {
					// 节点改变
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						watchService(zk);
					}
				}
			});
			Map<String, List<String>> servicesMapTemp = new ConcurrentHashMap<String, List<String>>();
			// 循环子节点
			for (String service : servicesList) {
				// 获取节点中的服务器地址
				// byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/"
				// + service, false, null);
				// 存储到list中
				// dataList.add(new String(bytes));
				List<String> dataList = new ArrayList<String>();
				List<String> nodesList = watchNode(service, zk);
				if (nodesList != null) {
					for (String data : nodesList) {
						byte[] bytes = zk.getData(Constant.ZK_REGISTRY__BASE_PATH + "/" + service + "/" + data, false, null);
						dataList.add(new String(bytes));
					}
					LOGGER.debug("{} node data: {}", service, dataList);
					servicesMapTemp.put(service, dataList);
				}
			}
			// 将节点信息记录在成员变量
			this.servicesMap = servicesMapTemp;
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * 通过服务名称获取服务列表
	 * 
	 * @param service
	 * @param zk
	 * @return
	 */
	private List<String> watchNode(String service, ZooKeeper zk) {
		try {
			List<String> servicesList = zk.getChildren(Constant.ZK_REGISTRY__BASE_PATH + "/" + service, new Watcher() {
				public void process(WatchedEvent event) {
					// 节点改变
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						watchService(zk);
					}
				}
			});
			return servicesList;
		} catch (Exception e) {
			LOGGER.error("", e);
			return null;
		}
	}

}
