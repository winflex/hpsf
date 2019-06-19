package io.hpsf.rpc.client;

import java.util.concurrent.TimeUnit;

import io.hpsf.common.concurrent.IFuture;
import io.hpsf.rpc.common.IInvoker;
import io.hpsf.rpc.common.Invocation;
import io.hpsf.rpc.common.RpcException;

/**
 * 远程调用
 * 
 * @author winflex
 */
public class ClientInvoker<T> implements IInvoker<T> {

	private final RpcClient rpcClient;
	private final Class<T> iface;

	public ClientInvoker(Class<T> iface, RpcClient rpcClient) throws RpcException {
		this.rpcClient = rpcClient;
		this.iface = iface;
	}

	@Override
	public Object invoke(Invocation inv) throws Throwable {
		IFuture<Object> future = rpcClient.send(inv);
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
