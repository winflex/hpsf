package io.hpsf.rpc.common;

/**
 * 
 * @author winflex
 */
public interface IInvoker<T> {
	
	Object invoke(Invocation invocation) throws Throwable;

	Class<T> getInterface();
}
