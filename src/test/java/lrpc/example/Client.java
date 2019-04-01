package lrpc.example;

import lrpc.client.RpcClient;
import lrpc.common.RpcException;
import lrpc.util.Endpoint;

/**
 * 
 * @author winflex
 */
public class Client {
	public static void main(String[] args) throws RpcException {
		RpcClient client = new RpcClient(new Endpoint("localhost", 9999));
		AddService service = client.getProxy(AddService.class);
		System.out.println(service.add(1, 2));
		
	}
}
