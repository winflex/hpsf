package io.hpsf.rpc.protocol;

import io.hpsf.rpc.RpcResult;

/**
 * 
 * @author winflex
 */
public class RpcResponse extends RpcMessage<RpcResult>{

	private static final long serialVersionUID = -2129597413059665223L;

	public RpcResponse(long id, RpcResult result) {
		setType(TYPE_INVOKE_RESPONSE);
		setId(id);
		setData(result);
	}
}
