package lrpc.common;

import lrpc.util.concurrent.IFuture;

/**
 * 
 * @author winflex
 */
public interface IAsyncInvoker<T> extends IInvoker<T> {
	
	@Override
	IFuture<?> invoke(Invocation invocation) throws Throwable;
	
}
