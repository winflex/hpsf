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

	public DefaultInvoker(Class<T> iface, RpcClient rpcClient) throws RpcException {
		this.rpcClient = rpcClient;
		this.iface = iface;
	}

	@Override
	public Object invoke(Invocation inv) throws Throwable {
		Future<Object> future = rpcClient.send(inv);
		RpcContext ctx = RpcContext.getContext();
		inv.setAttachments(ctx.getAttachments());
		if (ctx.isAsync()) {
			ctx.setFuture(future);
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
