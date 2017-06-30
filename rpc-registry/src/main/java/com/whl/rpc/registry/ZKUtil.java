package com.whl.rpc.registry;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @description: zookeeper连接的工具类
 * @author whling
 * @date 2017年6月29日上午10:02:07
 *
 */
public class ZKUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZKUtil.class);
	//初始化闭锁的等待线程数为1
	private static CountDownLatch latch = new CountDownLatch(1);

	public static ZooKeeper connectServer(String serverAddress) {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(serverAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					/**
					 * 表示当watcher事件监听成功时，latch计数器减1
					 */
					if (event.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown();
					}
				}
			});
			//主线程阻塞，当latch中计数器为0的时候，可继续执行
			latch.await();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return zk;
	}
}
