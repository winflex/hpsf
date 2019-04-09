package lrpc.common.protocol;

import java.io.Serializable;

/**
 * 
 * @author winflex
 */
public class RpcMessage<T> implements Serializable {
	
	private static final long serialVersionUID = -8323120792660898305L;
	
	public static final byte TYPE_INVOKE_REQUEST = 1;
	public static final byte TYPE_INVOKE_RESPONSE = -1;
	public static final byte TYPE_HEARTBEAT = 2;
	public static final byte TYPE_SYNC = 3;
	
	private byte type;
	private long id;
	private T data;
	
//	public RpcMessage() {
//	}
//
//	public RpcMessage(byte type, long id, Object data) {
//		this.type = type;
//		this.id = id;
//		this.data = data;
//	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
