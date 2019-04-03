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
		server.publish(AddService.class, new AddServiceImpl());
		new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				server.close();
			};
		}.start();
		server.start().closeFuture().awaitUninterruptibly();
		
		
	}
}
