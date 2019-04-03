package lrpc.client;

import java.util.concurrent.Callable;

import lrpc.util.concurrent.DefaultPromise;
import lrpc.util.concurrent.IFuture;

/**
 * 
 * @author winflex
 */
public class RpcContext {
	
	private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };
	
	public static RpcContext getContext() {
        return LOCAL.get();
    }
	
	public static void removeContext() {
        LOCAL.remove();
    }
	
	
	private boolean async;
	
	private IFuture<?> future;
	
	@SuppressWarnings("unchecked")
	public <T> IFuture<T> asyncCall(Callable<T> callable) {
		this.async = true;
		try {
			callable.call();
		} catch (Throwable e) {
			if (future == null) {
				DefaultPromise<T> future = new DefaultPromise<>();
				future.setFailure(e);
				return future;
			}
		} finally {
			async = false;
		}
		return (IFuture<T>) this.future;
	}

	public final boolean isAsync() {
		return async;
	}

	public final IFuture<?> getFuture() {
		return future;
	}

	public final void setFuture(IFuture<?> future) {
		this.future = future;
	}
}
