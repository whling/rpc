# rpc
# 自定义rpc框架的实现

手动实现类似dubbo框架的核心功能，实现远程过程调用 

`使用技术`：zookeeper+spring4.3.8+netty4.0.24+protostuff1.0.8   <br/>

`使用方式`：   <br/>
1.导入工程，convert to maven project<br/>
2.本地启动zookeeper服务，客户端服务端口默认为2181<br/>
3.启动rpc-app-server，将服务发布到zookeeper中<br/>
4.运行rpc-app-client服务消费者<br/>
说明：可以自己修改配置文件改变zookeeper位置
