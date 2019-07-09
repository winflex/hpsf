package io.hpsf.rpc.client.proxy;

import io.hpsf.rpc.common.Invoker;
import io.hpsf.rpc.common.RpcException;

/**
 * 
 * 
 * @author winflex
 */
public interface ProxyFactory {

	<T> T getProxy(Invoker<T> invoker) throws RpcException;
}