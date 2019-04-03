package lrpc.common.protocol;

/**
 * 
 * @author winflex
 */
public class HeartbeatMessage extends RpcMessage<Void> {

	private static final long serialVersionUID = -1247463650322963360L;

	
	public HeartbeatMessage() {
		setType(TYPE_HEARTBEAT_REQUEST);
	}
}
