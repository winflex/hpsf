package io.hpsf.rpc.benchmark.lrpc;

import io.hpsf.rpc.common.RpcException;
import io.hpsf.rpc.example.AddService;
import io.hpsf.rpc.example.AddServiceImpl;
import io.hpsf.rpc.server.RpcServer;

/**
 * 
 * @author winflex
 */
public class Server {
	public static void main(String[] args) throws RpcException {
		RpcServer server = new RpcServer(9999);
		server.register(AddService.class, new AddServiceImpl());
		server.start().closeFuture().awaitUninterruptibly();
	}
}
