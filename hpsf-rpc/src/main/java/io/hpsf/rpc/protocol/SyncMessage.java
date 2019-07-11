package io.hpsf.rpc.protocol;

/**
 * 
 * @author winflex
 */
public class SyncMessage extends RpcMessage<ServerInfo> {

	private static final long serialVersionUID = 1547128260024417442L;

	public SyncMessage(ServerInfo serverInfo) {
		setType(TYPE_SYNC);
		setData(serverInfo);
	}
}
