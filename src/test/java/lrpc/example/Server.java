package lrpc.example;

import java.io.IOException;

import lrpc.common.RpcException;
import lrpc.server.RpcServer;

/**
 * 
 * @author winflex
 */
public class Server {
	public static void main(String[] args) throws RpcException, IOException {
		RpcServer server = new RpcServer(9999);
		server.publish(AddService.class.getName(), new AddServiceImpl());
		server.start().closeFuture().awaitUninterruptibly();
	}
}
