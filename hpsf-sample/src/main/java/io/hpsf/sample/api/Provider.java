package io.hpsf.sample.api;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.hpsf.common.concurrent.NamedThreadFactory;
import io.hpsf.rpc.provider.PropertiesRpcServerConfig;
import io.hpsf.rpc.provider.RpcServer;
import io.hpsf.rpc.provider.RpcServerConfig;

/**
 * 
 * @author winflex
 */
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
