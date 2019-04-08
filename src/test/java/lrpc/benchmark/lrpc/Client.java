package lrpc.benchmark.lrpc;

import lrpc.benchmark.LoadRunner;
import lrpc.client.RpcClient;
import lrpc.example.AddService;
import lrpc.util.Endpoint;

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
