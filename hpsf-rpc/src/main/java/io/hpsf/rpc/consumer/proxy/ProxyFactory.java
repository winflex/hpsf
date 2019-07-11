package io.hpsf.rpc.consumer.proxy;

import io.hpsf.rpc.Invoker;

/**
 * 
 * 
 * @author winflex
 */
public interface ProxyFactory {

	<T> T getProxy(Invoker<T> invoker);
}