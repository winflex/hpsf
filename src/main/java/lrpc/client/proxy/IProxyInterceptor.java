package lrpc.client.proxy;

import java.lang.reflect.Method;

import lrpc.common.IInvoker;
import lrpc.common.Invocation;

/**
 * The generic proxy procedure.
 * 
 * @author winflex
 */
public interface IProxyInterceptor {
	
	public static final IProxyInterceptor INSTANCE = new IProxyInterceptor() {};
	
	default Object intercept(IInvoker<?> invoker, Object proxy, Method method, Object[] args) throws Throwable {
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
}
