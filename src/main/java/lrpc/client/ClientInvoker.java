package lrpc.client;

import java.util.concurrent.TimeUnit;

import lrpc.common.IInvoker;
import lrpc.common.Invocation;
import lrpc.common.RpcException;
import lrpc.util.concurrent.IFuture;

/**
 * The remoting invoker
 * 
 * @author winflex
 */
public class ClientInvoker<T> implements IInvoker<T> {

	private final RpcClient rpcClient;
	private final Class<T> iface;

	public ClientInvoker(RpcClient rpcClient, Class<T> iface) throws RpcException {
		this.rpcClient = rpcClient;
		this.iface = iface;
	}

	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		IFuture<Object> future = rpcClient.send(invocation, true);
		return future.get(rpcClient.getOptions().getRequestTimeoutMillis(), TimeUnit.MILLISECONDS);
	}

	@Override
	public Class<T> getInterface() {
		return iface;
	}
}
