package lrpc.common.protocol;

/**
 * 
 * @author winflex
 */
public class RpcMessage {
	
	public static final byte TYPE_INVOKE = 1;
	public static final byte TYPE_INVOKE_RESPONSE = -1;
	public static final byte TYPE_HEARTBEAT = 2;
	
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
