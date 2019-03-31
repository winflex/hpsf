/**
 * 
 */
package lrpc.client;

import java.lang.reflect.Proxy;

import lrpc.util.URL;

/**
 *
 * @author winflex
 */
public class RpcClient implements AutoCloseable {

	private final String ip;
	private final int port;
	private final int ioThreads;

	public RpcClient(String ip, int port, int ioThreads) {
		this.ip = ip;
		this.port = port;
		this.ioThreads = ioThreads;
	}

	@Override
	public void close() throws Exception {

	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(URL url, Class<T> iface) {
		ClassLoader cl = getClass().getClassLoader();
		Class<?>[] ifaces = new Class[] { iface };
		return (T) Proxy.newProxyInstance(cl, ifaces, new InvokeInvocationHandler(this, url));
	}
}
