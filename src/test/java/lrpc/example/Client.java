package lrpc.example;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import lrpc.client.RpcClient;
import lrpc.client.RpcContext;
import lrpc.util.Endpoint;
import lrpc.util.concurrent.IFuture;
import lrpc.util.concurrent.IFutureListener;

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
		IFuture<Integer> future = RpcContext.getContext().asyncCall(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return service.add(1, 2);
			}
		});
		CountDownLatch latch = new CountDownLatch(1);
		future.addListener(new IFutureListener<IFuture<Integer>>() {

			@Override
			public void operationCompleted(IFuture<Integer> future) throws Exception {
				if (future.isSuccess()) {
					int result = future.getNow();
					System.out.println("async result: " + result);
				} else {
					future.cause().printStackTrace();
				}
				latch.countDown();
			}
		});
		latch.await();
		client.close();
	}
}
