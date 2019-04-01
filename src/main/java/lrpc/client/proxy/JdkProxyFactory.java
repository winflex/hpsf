package lrpc.client.proxy;

import java.lang.reflect.Proxy;

import lrpc.client.InvokeInvocationHandler;
import lrpc.common.IInvoker;
import lrpc.common.RpcException;

/**
 * 
 * @author winflex
 */
public class JdkProxyFactory implements IProxyFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy(IInvoker<T> invoker) throws RpcException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<?>[] interfaces = new Class<?>[] { invoker.getInterface() };
		InvokeInvocationHandler handler = new InvokeInvocationHandler(invoker);
		return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
	}
}
