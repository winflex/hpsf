package lrpc.client;

import lrpc.util.concurrent.IFuture;

/**
 * 
 *
 * @author winflex
 */
public interface IConnection extends AutoCloseable {
	
	default IFuture<Object> send(Object req) {
		return send(req, true);
	}
	
	IFuture<Object> send(Object req, boolean needReply);
	
}
