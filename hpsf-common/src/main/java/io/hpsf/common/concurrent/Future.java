package io.hpsf.common.concurrent;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An enhanced {@link Future} that supports callback and attachments
 *
 * @author winflex
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

	V getNow();

	@Override
	V get() throws InterruptedException, ExecutionException;

	@Override
	V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	boolean isSuccess();

	Throwable cause();

	Future<V> addListener(FutureListener<? extends Future<V>> listener);

	Future<V> addListener(FutureListener<? extends Future<V>> listener, Executor executor);

	Future<V> removeListener(FutureListener<? extends Future<V>> listener);

	Future<V> await() throws InterruptedException;

	Future<V> awaitUninterruptibly();

	boolean await(long timeout, TimeUnit unit) throws InterruptedException;

	boolean awaitUninterruptibly(long timeout, TimeUnit unit);

	Object getAttachment(String name);

	Map<String, Object> getAttachments();

	Future<V> setAttachment(String name, Object value);
}
