package lrpc.server;

import java.lang.reflect.Method;

import lrpc.common.IInvoker;
import lrpc.common.Invocation;
import lrpc.common.RpcException;
import lrpc.server.IServiceRepository.Publishment;

/**
 * 本地调用
 * 
 * @author winflex
 */
public class ServerInvoker<T> implements IInvoker<T> {

	private final Class<T> iface;
	private final RpcServer rpcServer;

	public ServerInvoker(Class<T> iface, RpcServer rpcServer) {
		this.iface = iface;
		this.rpcServer = rpcServer;
	}

	@Override
	public Object invoke(Invocation inv) throws Throwable {
		Publishment publishment = rpcServer.get(inv.getClassName());
		if (publishment == null) {
			throw new RpcException(inv.getClassName() + " is not published");
		}
		
		Object instance = publishment.getInstance(); // the service instance
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
