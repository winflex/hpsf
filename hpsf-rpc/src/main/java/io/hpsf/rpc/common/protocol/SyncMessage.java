package io.hpsf.rpc.common.protocol;

import io.hpsf.rpc.common.ServerInfo;

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
