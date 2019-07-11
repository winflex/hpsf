package io.hpsf.rpc.consumer.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.Invoker;

/**
 * 
 * @author winflex
 */
public class DefaultProxyFactory implements ProxyFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy(Invoker<T> invoker) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<?>[] interfaces = new Class<?>[] { invoker.getInterface() };
		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (method.getDeclaringClass() == Object.class) {
					return method.invoke(invoker, args);
				}
				if ("toString".equals(methodName) && parameterTypes.length == 0) {
					return invoker.toString();
				}
				if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
					return invoker.hashCode();
				}
				if ("equals".equals(methodName) && parameterTypes.length == 1) {
					return invoker.equals(args[0]);
				}
				Invocation inv = new Invocation();
				inv.setClassName(invoker.getInterface().getName());
				inv.setMethodName(method.getName());
				inv.setParameterTypes(method.getParameterTypes());
				inv.setParemeters(args);
				return invoker.invoke(inv);
			}
		};
		return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
	}
}
