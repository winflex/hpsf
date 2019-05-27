package io.hpsf.rpc.common.protocol;

import java.util.concurrent.atomic.AtomicLong;

import io.hpsf.rpc.common.Invocation;

/**
 * 
 * @author winflex
 */
public class RpcRequest extends RpcMessage<Invocation> {

	private static final long serialVersionUID = -3291542680694765400L;

	public RpcRequest(Invocation inv) {
		this(sequence.incrementAndGet(), inv);
	}
	
	public RpcRequest(long id, Invocation inv) {
		setType(TYPE_INVOKE_REQUEST);
		setId(id);
		setData(inv);
	}
	
	private static final AtomicLong sequence = new AtomicLong();
}
