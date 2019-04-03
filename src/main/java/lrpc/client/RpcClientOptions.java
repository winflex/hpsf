package lrpc.client;

/**
 * 
 * @author winflex
 */
public class RpcClientOptions {
	
	private int ioThreads;
	
	private int connectTimeoutMillis = 3000;
	
	private int requestTimeoutMillis = Integer.MAX_VALUE; // default to forever
	
	private int maxConnections = 1;

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getIoThreads() {
		return ioThreads;
	}

	public void setIoThreads(int ioThreads) {
		this.ioThreads = ioThreads;
	}

	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	public void setConnectTimeoutMillis(int connectTimeoutMillis) {
		if (connectTimeoutMillis <= 0) {
			throw new IllegalArgumentException("The connectTimeoutMillis must be positive");
		}
		this.connectTimeoutMillis = connectTimeoutMillis;
	}

	public int getRequestTimeoutMillis() {
		return requestTimeoutMillis;
	}

	public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
		if (invokeTimeoutMillis <= 0) {
			throw new IllegalArgumentException("The invokeTimeoutMillis must be positive");
		}
		this.requestTimeoutMillis = invokeTimeoutMillis;
	}
}
