package io.hpsf.rpc.example;

import java.util.concurrent.CountDownLatch;

import io.hpsf.common.Endpoint;
import io.hpsf.common.concurrent.IFuture;
import io.hpsf.rpc.client.RpcClient;
import io.hpsf.rpc.client.RpcContext;

/**
 * 
 * @author winflex
 */
public class Client {
	public static void main(String[] args) throws Exception {
		RpcClient client = new RpcClient(new Endpoint("localhost", 9999));
		AddService service = client.getProxy(AddService.class);
		
		// 同步调用
		int result = service.add(1, 2);
		System.out.println("sync result: " + result);
		
		// 异步调用
		IFuture<Integer> future = RpcContext.getContext().asyncCall(() -> service.add(1, 2));
		CountDownLatch latch = new CountDownLatch(1);
		future.addListener((f) -> {
			if (future.isSuccess()) {
				System.out.println("async result: " + future.getNow());
			} else {
				future.cause().printStackTrace();
			}
			latch.countDown();
		});
		// 等待异步调用完成
		latch.await();
		
		// 关闭RPC客户端
		client.close();
	}
}
