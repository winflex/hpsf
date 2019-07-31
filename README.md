# 示例

## 与spring集成使用
### 提供者

```
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:hpsf="http://www.hpsf-rpc.org/hpsf"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.hpsf-rpc.org/hpsf
       http://www.hpsf-rpc.org/hpsf/hpsf.xsd">

	<!-- 服务实现实例 -->
	<bean id="addServiceImpl" class="io.hpsf.sample.api.AddServiceImpl" />

	<!-- 配置RPC服务器 -->
	<hpsf:server port="8888" registry="zookeeper://127.0.0.1:2181" />

	<!-- 配置暴露的服务 -->
	<hpsf:service ref="addServiceImpl" version="1.0">
		<!-- 该服务独享的线程池配置(可选) -->
		<hpsf:threadPool corePoolSize="1" maxPoolSize="1" queueSize="5" threadName="AddServiceWorker" keepAliveTime="10" allowCoreThreadTimeout="false"/>
	</hpsf:service>
</beans>
```

```
public class SpringProvider {
	public static void main(String[] args) throws Throwable {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-provider.xml");
		System.in.read();
		ctx.close();
	}
}
```

### 消费者

```
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:hpsf="http://www.hpsf-rpc.org/hpsf"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.hpsf-rpc.org/hpsf
       http://www.hpsf-rpc.org/hpsf/hpsf.xsd">

	<!-- 配置RPC客户端 -->
	<hpsf:client registry="zookeeper://127.0.0.1:2181" />

	<!-- 接口调用代理 -->
	<hpsf:reference id="addService" interface="io.hpsf.sample.api.AddService" version="1.0" />

	<!-- 泛化调用代理 -->
	<hpsf:reference id="genericService" interface="io.hpsf.sample.api.AddService" version="1.0" generic="true" />
</beans>
```

```
public class SpringConsumer {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-consumer.xml");
		// 接口调用
		AddService service = (AddService) ctx.getBean("addService");
		int result = service.add(1, 2);
		System.out.println("invoke result: " + result);
		//泛化调用
		GenericService genericService = (GenericService) ctx.getBean("genericService");
		int genericResult = (int) genericService.$invoke("add", new Class<?>[] { int.class, int.class }, new Object[] { 1, 2 });
		System.out.println("genertic invoke result: " + genericResult);
		ctx.close();
	}
}
```

## API方式

### 提供者
```
public class Provider {
	
	public static void main(String[] args) throws Exception {
//		RpcServerConfig config = newConfigByAPI();
		RpcServerConfig config = newConfigByClasspathPropertiesFile();
		RpcServer server = new RpcServer(config);
		
		Executor executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("AddService-Executor"));
		AddService service = new AddServiceImpl();
		server.publish(AddService.class, service, "1.0", executor);
		server.join();
		server.close();
	}
	
	/**
	 * API方式生成RpcServerConfig
	 */
	static final RpcServerConfig newConfigByAPI() {
		return RpcServerConfig.builder().registry("zookeeper://localhost:2181").build();
	}
	
	/**
	 * 基于properties文件生成RpcServerConfig
	 */
	static final RpcServerConfig newConfigByPropertiesFile() throws IOException {
		return new PropertiesRpcServerConfig("/path/to/server.properties");
	}
	
	/**
	 * 基于classpath properties文件生成RpcServerConfig
	 */
	static final RpcServerConfig newConfigByClasspathPropertiesFile() throws IOException {
		return new PropertiesRpcServerConfig("classpath:server.properties");
	}
}
```
server.properties
```
#server.rpc.ip=127.0.0.1
server.rpc.port=9999
# server.rpc.ioThreads=0
# server.rpc.heartbeatInterval=10000
# server.rpc.serializer=hessian

server.registry=zookeeper://127.0.0.1:2181
```

### 消费者
```
public class Consumer {
	public static void main(String[] args) throws Exception {
		RpcClientConfig config = RpcClientConfig.builder().registry("zookeeper://127.0.0.1:2181").build();
		
		RpcClient client = new RpcClient(config);
		AddService service = client.getServiceProxy(AddService.class, "1.0");
		System.out.println("invoke result: " + service.add(1, 2));
		
		GenericService genericService = client.getGenericServiceProxy("io.hpsf.sample.api.AddService", "1.0");
		System.out.println("generic invoke result: " + genericService.$invoke("add", new Class<?>[] {int.class, int.class}, new Object[] {1, 2}));
		client.close();
	}
}
```

## TODO List
1. 实现eureka注册中心
