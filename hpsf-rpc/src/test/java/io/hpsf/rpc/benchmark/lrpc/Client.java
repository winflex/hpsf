package io.hpsf.rpc.benchmark.lrpc;

import io.hpsf.common.Endpoint;
import io.hpsf.rpc.benchmark.LoadRunner;
import io.hpsf.rpc.client.RpcClient;
import io.hpsf.rpc.example.AddService;

/**
 * 
 * @author winflex
 */
public class Client {
	public static void main(String[] args) throws Exception {
		RpcClient client = new RpcClient(new Endpoint("localhost", 9999));
		AddService service = client.getProxy(AddService.class);
		LoadRunner lb = LoadRunner.builder().reportInterval(1000).millis(600000).stopWhenError(true).threads(4)
				.transaction(() -> {
					service.add(1, 2);
				}).build();
		lb.run();
	}
}
