package io.hpsf.rpc.server;

import org.hpsf.registry.api.RegistryConfig;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class RpcServerConfig {
	
	public static final String DEFAULT_IP = "127.0.0.1";
	public static final int DEFAULT_PORT = 9999;
	public static final int DEFAULT_IO_THREADS = 0;
	public static final int DEFAULT_HEARTBEAT_INTERVAL = 10000;
	public static final String DEFAULT_SERIALIZER = "hessian";
	
	private int port = DEFAULT_PORT;
	
	private String ip = DEFAULT_IP;
	
	private int ioThreads = DEFAULT_IO_THREADS;
	
	private int heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
	
	private String serializer = DEFAULT_SERIALIZER;
	
	private RegistryConfig registryConfig;
	
}
