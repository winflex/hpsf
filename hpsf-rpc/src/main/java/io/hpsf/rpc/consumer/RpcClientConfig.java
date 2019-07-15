package io.hpsf.rpc.consumer;

import lombok.Data;

/**
 * RPC客户端配置
 * 
 * @author winflex
 */
@Data
public class RpcClientConfig {

	private String registry;
	
	/** io线程个数 */
	private int ioThreads;

	/** 创建连接超时时间 */
	private int connectTimeoutMillis = 3000;

	/** 请求超时时间 */
	private int requestTimeoutMillis = Integer.MAX_VALUE; // default to forever

	/** 最大连接数 */
	private int maxConnectionPerServer = 1;

	public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
		if (invokeTimeoutMillis <= 0) {
			throw new IllegalArgumentException("The invokeTimeoutMillis must be positive");
		}
		this.requestTimeoutMillis = invokeTimeoutMillis;
	}

	public void setConnectTimeoutMillis(int connectTimeoutMillis) {
		if (connectTimeoutMillis <= 0) {
			throw new IllegalArgumentException("The connectTimeoutMillis must be positive");
		}
		this.connectTimeoutMillis = connectTimeoutMillis;
	}
}
