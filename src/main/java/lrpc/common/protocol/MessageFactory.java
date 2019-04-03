package lrpc.common.protocol;

import static lrpc.common.protocol.RpcMessage.*;

import lrpc.common.Invocation;
import lrpc.common.RpcResult;
import lrpc.common.ServerInfo;

/**
 * 
 * @author winflex
 */
public class MessageFactory {
	
	public static RpcMessage<?> create(byte type, long id, Object data) {
		if (type == TYPE_INVOKE_REQUEST) {
			return new RpcRequest((Invocation) data);
		} else if (type == TYPE_INVOKE_RESPONSE) {
			return new RpcResponse(id, (RpcResult) data);
		} else if (type == TYPE_HEARTBEAT_REQUEST) {
			return new HeartbeatMessage();
		} else if (type == TYPE_INITIALIZE) {
			return new InitializeMessage((ServerInfo) data);
		}
		return null;
	}
	
}
