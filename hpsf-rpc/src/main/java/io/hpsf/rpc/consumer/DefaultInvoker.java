package io.hpsf.rpc.consumer;

import java.util.concurrent.TimeUnit;

import io.hpsf.common.concurrent.Future;
import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.Invoker;
import io.hpsf.rpc.RpcContext;
import io.hpsf.rpc.RpcException;

/**
 * 远程调用
 * 
 * @author winflex
 */
public class DefaultInvoker<T> implements Invoker<T> {

	private final RpcClient rpcClient;
	private final Class<T> iface;
	private final String serviceVersion;

	public DefaultInvoker(Class<T> iface, String serviceVersion, RpcClient rpcClient) throws RpcException {
		this.rpcClient = rpcClient;
		this.iface = iface;
		this.serviceVersion = serviceVersion;
	}

	@Override
	public Object invoke(Invocation inv) throws Throwable {
		RpcContext context = RpcContext.getContext();
		inv.setAttachments(context.getAttachments());
		inv.setVersion(serviceVersion);
		
		// 调用
		Future<Object> future = rpcClient.send(inv);
		if (context.isAsync()) {
			context.setFuture(future);
			return null;
		} else {
			return future.get(rpcClient.getOptions().getRequestTimeoutMillis(), TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public Class<T> getInterface() {
		return iface;
	}
}
