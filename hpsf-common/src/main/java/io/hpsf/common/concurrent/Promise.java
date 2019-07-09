package io.hpsf.common.concurrent;

import java.util.concurrent.Executor;

/**
 * A writeable {@link Future}
 * 
 * @author winflex
 */
public interface Promise<V> extends Future<V> {
    
    Promise<V> setSuccess(Object result);
    
    Promise<V> setFailure(Throwable cause);
    
    @Override
    Promise<V> addListener(FutureListener<? extends Future<V>> listener);
    
    @Override
    Promise<V> addListener(FutureListener<? extends Future<V>> listener, Executor executor);

    @Override
    Promise<V> removeListener(FutureListener<? extends Future<V>> listener);

    @Override
	Promise<V> await() throws InterruptedException;

    @Override
    Promise<V> awaitUninterruptibly();
    
    @Override
    Promise<V> setAttachment(String name, Object value);
}
