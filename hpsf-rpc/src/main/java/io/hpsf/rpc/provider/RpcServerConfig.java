package io.hpsf.rpc.provider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author winflex
 */
@Data
@Builder
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
public class RpcServerConfig {
	
	public static final int DEFAULT_PORT = 9999;
	public static final int DEFAULT_IO_THREADS = 0;
	public static final int DEFAULT_HEARTBEAT_INTERVAL = 10000;
	public static final String DEFAULT_SERIALIZER = "hessian";
	
	@Builder.Default
	private int port = DEFAULT_PORT;
	
	private String ip;
	
	@Builder.Default
	private int ioThreads = DEFAULT_IO_THREADS;
	
	@Builder.Default
	private int heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
	
	@Builder.Default
	private String serializer = DEFAULT_SERIALIZER;
	
	private String registry;
}
