package lrpc.server;

import java.util.concurrent.Executor;

import lombok.Data;

/**
 * RPC服务器配置
 * 
 * @author winflex
 */
@Data
public class RpcServerOptions {

	private int port;

	private String bindIp = "0.0.0.0";

	private int ioThreads = 0;

	private int heartbeatInterval = 10000;
	
	private Executor executor;
	
	private String serializer = "hessian";

	public RpcServerOptions(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("port must be positive");
		}
		this.port = port;
	}

	public void setSerializer(String serializer) {
		if (serializer == null) {
			throw new IllegalArgumentException("The serializer can't be null");
		}
		this.serializer = serializer;
	}
}
