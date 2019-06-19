package io.hpsf.rpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.hpsf.rpc.common.IInvoker;
import io.hpsf.rpc.common.RpcException;

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
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return IProxyInterceptor.INSTANCE.intercept(invoker, proxy, method, args);
			}
		};
		return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
	}
}
