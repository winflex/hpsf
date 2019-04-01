package lrpc.common;

/**
 * 
 * @author winflex
 */
public interface IInvoker<T> extends AutoCloseable {
	
	Object invoke(Invocation invocation) throws Throwable;

	Class<T> getInterface();
}
