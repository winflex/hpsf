package io.hpsf.rpc.provider;

import java.lang.reflect.Method;

import io.hpsf.registry.api.ServiceMeta;
import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.Invoker;
import io.hpsf.rpc.RpcException;

/**
 * 本地调用
 * 
 * @author winflex
 */
public class ServerInvoker<T> implements Invoker<T> {

	private final Class<T> iface;
	private final RpcServer rpcServer;

	public ServerInvoker(Class<T> iface, RpcServer rpcServer) {
		this.iface = iface;
		this.rpcServer = rpcServer;
	}

	@Override
	public Object invoke(Invocation inv) throws Throwable {
		ServiceMeta meta = new ServiceMeta(inv.getClassName(), inv.getVersion());
		Publishment publishment = rpcServer.lookup(meta);
		if (publishment == null) {
			throw new RpcException(meta.directoryString() + " is not published");
		}
		
		Object instance = publishment.getServiceInstance(); // the service instance
		Method method = instance.getClass().getMethod(inv.getMethodName(), inv.getParameterTypes());
		if (method == null) {
			throw new Exception(inv.getClassName() + "." + inv.getMethodName()
					+ " is not published");
		}
		return method.invoke(instance, inv.getParemeters());
	}

	@Override
	public Class<T> getInterface() {
		return iface;
	}
}
