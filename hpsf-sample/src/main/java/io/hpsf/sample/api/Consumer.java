package io.hpsf.sample.api;

import io.hpsf.rpc.consumer.GenericService;
import io.hpsf.rpc.consumer.RpcClient;
import io.hpsf.rpc.consumer.RpcClientConfig;

/**
 * 
 * @author winflex
 */
public class Consumer {
	public static void main(String[] args) throws Exception {
		RpcClientConfig config = new RpcClientConfig();
		config.setRegistry("zookeeper");
		config.setRegistryConnectString("127.0.0.1:2181");
		
		RpcClient client = new RpcClient(config);
		AddService service = client.getServiceProxy(AddService.class, "1.0");
		System.out.println("invoke result: " + service.add(1, 2));
		
		GenericService genericService = client.getGenericServiceProxy("io.hpsf.sample.api.AddService", "1.0");
		System.out.println("generic invoke result: " + genericService.$invoke("add", new Class<?>[] {int.class, int.class}, new Object[] {1, 2}));
		client.close();
	}
}
