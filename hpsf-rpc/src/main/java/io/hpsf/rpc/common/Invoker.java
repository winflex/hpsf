package io.hpsf.rpc.common;

/**
 * 
 * @author winflex
 */
public interface Invoker<T> {
	
	Object invoke(Invocation invocation) throws Throwable;

	Class<T> getInterface();
}
