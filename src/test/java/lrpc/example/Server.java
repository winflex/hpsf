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
		server.register(AddService.class, new AddServiceImpl());
		server.start().closeFuture().awaitUninterruptibly();
	}
}
