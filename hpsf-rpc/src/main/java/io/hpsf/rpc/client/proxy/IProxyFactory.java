package io.hpsf.rpc.client.proxy;

import io.hpsf.rpc.common.IInvoker;
import io.hpsf.rpc.common.RpcException;

/**
 * 
 * 
 * @author winflex
 */
public interface IProxyFactory {

	<T> T getProxy(IInvoker<T> invoker) throws RpcException;
}