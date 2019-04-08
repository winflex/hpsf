package lrpc.benchmark.lrpc;

import lrpc.common.RpcException;
import lrpc.example.AddService;
import lrpc.example.AddServiceImpl;
import lrpc.server.RpcServer;

/**
 * 
 * @author winflex
 */
public class Server {
	public static void main(String[] args) throws RpcException {
		RpcServer server = new RpcServer(9999);
		server.publish(AddService.class, new AddServiceImpl());
		server.start().closeFuture().awaitUninterruptibly();
	}
}
