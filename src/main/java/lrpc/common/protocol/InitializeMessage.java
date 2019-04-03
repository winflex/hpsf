package lrpc.common.protocol;

import lrpc.common.ServerInfo;

/**
 * 
 * @author winflex
 */
public class InitializeMessage extends RpcMessage<ServerInfo> {

	private static final long serialVersionUID = 1547128260024417442L;

	public InitializeMessage(ServerInfo serverInfo) {
		setType(TYPE_INITIALIZE);
		setData(serverInfo);
	}
}
