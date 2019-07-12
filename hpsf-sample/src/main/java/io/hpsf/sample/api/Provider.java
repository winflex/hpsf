package io.hpsf.sample.api;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.hpsf.common.concurrent.NamedThreadFactory;
import io.hpsf.rpc.provider.ClassPathPropertiesRpcServerConfig;
import io.hpsf.rpc.provider.RpcServer;
import io.hpsf.rpc.provider.RpcServerConfig;

/**
 * 
 * @author winflex
 */
public class Provider {
	
	public static void main(String[] args) throws Exception {
		RpcServerConfig config = new ClassPathPropertiesRpcServerConfig("server.properties");
		RpcServer server = new RpcServer(config);
		
		Executor executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("AddService-Executor"));
		AddService service = new AddServiceImpl();
		server.publish(AddService.class, service, "1.0", executor);
		server.publish(AddService.class, service, "1.0", executor);
		server.join();
		server.close();
	}
	
}
