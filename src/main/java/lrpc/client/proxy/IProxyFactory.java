package lrpc.client.proxy;

import lrpc.common.IInvoker;
import lrpc.common.RpcException;

/**
 * 
 * 
 * @author winflex
 */
public interface IProxyFactory {

	<T> T getProxy(IInvoker<T> invoker) throws RpcException;
}