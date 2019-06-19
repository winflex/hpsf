package io.hpsf.rpc.client;

import io.hpsf.common.Endpoint;
import lombok.Data;

/**
 * RPC客户端配置
 * 
 * @author winflex
 */
@Data
public class RpcClientOptions {

	/** 服务端地址 */
	private Endpoint endpoint;

	/** io线程个数 */
	private int ioThreads;

	/** 创建连接超时时间 */
	private int connectTimeoutMillis = 3000;

	/** 请求超时时间 */
	private int requestTimeoutMillis = Integer.MAX_VALUE; // default to forever

	/** 最大连接数 */
	private int maxConnections = 1;

	/** 序列化扩展点名字 */
	private String serializer = "hessian";

	/** 动态代理扩展点名字, 默认使用jdk */
	private String proxy = "jdk";

	public RpcClientOptions(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

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

	public void setSerializer(String serializer) {
		if (serializer == null || serializer.isEmpty()) {
			throw new IllegalArgumentException("The serializer can't be null");
		}
		this.serializer = serializer;
	}

	public void setProxy(String proxy) {
		if (proxy == null || proxy.isEmpty()) {
			throw new IllegalArgumentException("The proxy can't be null");
		}
		this.proxy = proxy;
	}
}
