/**
 * 
 */
package lrpc.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lrpc.client.proxy.IProxyFactory;
import lrpc.client.proxy.JdkProxyFactory;
import lrpc.common.IInvoker;
import lrpc.common.RpcException;
import lrpc.util.Endpoint;

/**
 *
 * @author winflex
 */
public class RpcClient implements AutoCloseable {

	private final RpcClientConfig config;
	private final Endpoint endpoint;
	private final EventLoopGroup workerGroup; // The shared netty io thread pool

	
	public RpcClient(Endpoint endpoint) {
		this(endpoint, new RpcClientConfig());
	}

	public RpcClient(Endpoint endpoint, RpcClientConfig config) {
		this.config = config;
		this.endpoint = endpoint;
		this.workerGroup = new NioEventLoopGroup(config.getIoThreads());
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> iface) throws RpcException {
		IProxyFactory proxyFactory = new JdkProxyFactory();
		IInvoker<?> invoker = new RemoteInvoker<>(endpoint, iface, 1, config.getConnectTimeoutMillis(),
				config.getInvokeTimeoutMillis(), workerGroup);
		return (T) proxyFactory.getProxy(invoker);
	}

	@Override
	public void close() throws Exception {
		workerGroup.shutdownGracefully();
	}
}
