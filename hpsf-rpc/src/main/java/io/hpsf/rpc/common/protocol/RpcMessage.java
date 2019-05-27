package io.hpsf.rpc.common.protocol;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class RpcMessage<T> implements Serializable {
	
	private static final long serialVersionUID = -8323120792660898305L;
	
	public static final byte TYPE_INVOKE_REQUEST = 1;
	public static final byte TYPE_INVOKE_RESPONSE = -1;
	public static final byte TYPE_HEARTBEAT = 2;
	public static final byte TYPE_SYNC = 3;
	
	private byte type;
	private long id;
	private T data;
}
