package lrpc.client;

/**
 * 
 * @author winflex
 */
public class RpcClientConfig {
	
	private int ioThreads;
	
	private long connectTimeoutMillis = 3000;
	
	private long invokeTimeoutMillis; // 0 means forever

	public int getIoThreads() {
		return ioThreads;
	}

	public void setIoThreads(int ioThreads) {
		this.ioThreads = ioThreads;
	}

	public long getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	public void setConnectTimeoutMillis(long connectTimeoutMillis) {
		this.connectTimeoutMillis = connectTimeoutMillis;
	}

	public long getInvokeTimeoutMillis() {
		return invokeTimeoutMillis;
	}

	public void setInvokeTimeoutMillis(long invokeTimeoutMillis) {
		this.invokeTimeoutMillis = invokeTimeoutMillis;
	}
}
