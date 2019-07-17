package io.hpsf.rpc.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC客户端配置
 * 
 * @author winflex
 */
@Data
@Builder
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
public class RpcClientConfig {

	private String registry;
	
	/** io线程个数 */
	private int ioThreads;

	/** 创建连接超时时间 */
	@Builder.Default
	private int connectTimeoutMillis = 3000;

	/** 请求超时时间 */
	@Builder.Default
	private int requestTimeoutMillis = Integer.MAX_VALUE; // default to forever

	/** 最大连接数 */
	@Builder.Default
	private int maxConnectionPerServer = 1;
}
