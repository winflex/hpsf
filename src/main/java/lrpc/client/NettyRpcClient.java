/**
 * 
 */
package lrpc.client;

import static lrpc.common.protocol.RpcMessage.TYPE_INVOKE_REQUEST;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
import lrpc.common.IInvoker;
import lrpc.common.RpcException;
import lrpc.common.codec.Decoder;
import lrpc.common.codec.Encoder;
import lrpc.common.protocol.RpcMessage;
import lrpc.util.Endpoint;
import lrpc.util.concurrent.IFuture;

/**
 *
 * @author winflex
 */
public class NettyRpcClient implements AutoCloseable, RpcClient {

	private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

	private final RpcClientOptions options;
	private final Endpoint endpoint;
	private final EventLoopGroup workerGroup;
	private final ChannelPool channelPool;

	private final AtomicBoolean closed = new AtomicBoolean();

	public NettyRpcClient(Endpoint endpoint) {
		this(endpoint, new RpcClientOptions());
	}

	public NettyRpcClient(Endpoint endpoint, RpcClientOptions options) {
		this.options = options;
		this.endpoint = endpoint;
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
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.remoteAddress(endpoint.getIp(), endpoint.getPort());
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnectTimeoutMillis());
		return b;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> iface) throws RpcException {
		IProxyFactory proxyFactory = new JdkProxyFactory();
		IInvoker<?> invoker = new ClientInvoker<>(this, iface);
		return (T) proxyFactory.getProxy(invoker);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IFuture<T> send(Object data, boolean needReply) {
		long requestId = sequence.getAndIncrement();
		final ResponseFuture future = new ResponseFuture(requestId, options.getRequestTimeoutMillis());
		RpcMessage request = new RpcMessage(TYPE_INVOKE_REQUEST, requestId, data);
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

	@Override
	public void close() throws Exception {
		if (!closed.compareAndSet(false, true)) {
			return;
		}

		workerGroup.shutdownGracefully();
		channelPool.close();
	}

	@Override
	public final RpcClientOptions getOptions() {
		return options;
	}

	@Override
	public final Endpoint getEndpoint() {
		return endpoint;
	}

	private static final AtomicLong sequence = new AtomicLong();
}
