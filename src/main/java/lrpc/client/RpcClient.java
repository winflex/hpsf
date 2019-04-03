package lrpc.client;

import lrpc.common.RpcException;
import lrpc.util.Endpoint;
import lrpc.util.concurrent.IFuture;

/**
 * 
 * @author winflex
 */
public interface RpcClient {

	<T> T getProxy(Class<T> iface) throws RpcException;
	
	default <V> IFuture<V> send(Object data) {
		return send(data, true);
	}

	<V> IFuture<V> send(Object data, boolean needReply);

	RpcClientOptions getOptions();

	Endpoint getEndpoint();
}