package io.hpsf.rpc.provider;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class RpcServerConfig {
	
	public static final int DEFAULT_PORT = 9999;
	public static final int DEFAULT_IO_THREADS = 0;
	public static final int DEFAULT_HEARTBEAT_INTERVAL = 10000;
	public static final String DEFAULT_SERIALIZER = "hessian";
	
	private int port = DEFAULT_PORT;
	
	private String ip;
	
	private int ioThreads = DEFAULT_IO_THREADS;
	
	private int heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
	
	private String serializer = DEFAULT_SERIALIZER;
	
	private String registry;
}
