/**
 * 
 */
package lrpc.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import lrpc.client.proxy.IProxyFactory;
import lrpc.client.proxy.JdkProxyFactory;
import lrpc.common.Invocation;
import lrpc.common.RpcException;
import lrpc.common.codec.Decoder;
import lrpc.common.codec.Encoder;
import lrpc.common.protocol.RpcRequest;
import lrpc.util.Endpoint;
import lrpc.util.concurrent.IFuture;

/**
 *
 * @author winflex
 */
public class RpcClient {

	private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

	private final RpcClientOptions options;
	private final EventLoopGroup workerGroup;
	private final ChannelPool channelPool;

	private final AtomicBoolean closed = new AtomicBoolean();

	public RpcClient(Endpoint endpoint) {
		this(new RpcClientOptions(endpoint));
	}

	public RpcClient(RpcClientOptions options) {
		this.options = options;
		this.workerGroup = new NioEventLoopGroup(options.getIoThreads());
		this.channelPool = new FixedChannelPool(createBootstrap(), new AbstractChannelPoolHandler() {

			@Override
			public void channelCreated(Channel ch) throws Exception {
				logger.info("Channel connected, channel = {}", ch);
				ch.closeFuture().addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						logger.info("Channel disconnected, channel = {}", ch);
					}
				});

				ChannelPipeline pl = ch.pipeline();
				pl.addLast(new Decoder());
				pl.addLast(new Encoder());
				pl.addLast(new ResponseHandler());
			}
		}, options.getMaxConnections());
	}

	private Bootstrap createBootstrap() {
		Endpoint endpoint = options.getEndpoint();
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.remoteAddress(endpoint.getIp(), endpoint.getPort());
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnectTimeoutMillis());
		return b;
	}

	public <T> T getProxy(Class<T> iface) throws RpcException {
		IProxyFactory proxyFactory = new JdkProxyFactory();
		return (T) proxyFactory.getProxy(new ClientInvoker<>(iface, this));
	}

	@SuppressWarnings("unchecked")
	<T> IFuture<T> send(Invocation inv, boolean needReply) {
		RpcRequest request = new RpcRequest(inv);
		final long requestId = request.getId();
		final ResponseFuture future = new ResponseFuture(requestId, options.getRequestTimeoutMillis());
		Channel channel = null;
		try {
			// TODO 这里不应该用连接池
			channel = channelPool.acquire().get(options.getRequestTimeoutMillis(), TimeUnit.MILLISECONDS);
			channelPool.release(channel);
		} catch (Exception e) {
			ResponseFuture.doneWithException(requestId, e);
		}
		if (channel != null) {
			channel.writeAndFlush(request).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					if (!f.isSuccess()) {
						ResponseFuture.doneWithException(requestId, f.cause());
					} else if (!needReply) {
						// need no reply from server, reply upon request sent
						ResponseFuture.doneWithResult(requestId, null);
					}
				}
			});
		}
		return (IFuture<T>) future;
	}

	public void close() {
		if (!closed.compareAndSet(false, true)) {
			return;
		}

		workerGroup.shutdownGracefully();
		channelPool.close();
	}

	public final RpcClientOptions getOptions() {
		return options;
	}
}
