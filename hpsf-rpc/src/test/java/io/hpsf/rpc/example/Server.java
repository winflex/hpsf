package io.hpsf.rpc.example;

import java.io.IOException;

import io.hpsf.rpc.common.RpcException;
import io.hpsf.rpc.server.RpcServer;

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
