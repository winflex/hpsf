package lrpc.common.protocol;

import java.io.Serializable;

/**
 * 
 * @author winflex
 */
public class RpcMessage implements Serializable {
	
	private static final long serialVersionUID = -8323120792660898305L;
	
	public static final byte TYPE_INVOKE_REQUEST = 1;
	public static final byte TYPE_INVOKE_RESPONSE = -1;
	public static final byte TYPE_HEARTBEAT_REQUEST = 2;
	public static final byte TYPE_HEARTBEAT_RESPONSE = -2;
	
	
	private byte type;
	private long id;
	private Object data;
	
	public RpcMessage() {
	}

	public RpcMessage(byte type, long id, Object data) {
		this.type = type;
		this.id = id;
		this.data = data;
	}

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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
