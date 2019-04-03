package lrpc.example;

import lrpc.client.NettyRpcClient;
import lrpc.common.RpcException;
import lrpc.util.Endpoint;

/**
 * 
 * @author winflex
 */
public class Client {
	public static void main(String[] args) throws Exception {
		NettyRpcClient client = new NettyRpcClient(new Endpoint("localhost", 9999));
		AddService service = client.getProxy(AddService.class);
		System.out.println(service.add(1, 2));
		System.in.read();
		System.out.println(service.add(1, 2));
		
	}
}
