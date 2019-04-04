/**
 * 
 */
package lrpc.client;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lrpc.client.proxy.IProxyFactory;
import lrpc.client.proxy.JdkProxyFactory;
import lrpc.common.Invocation;
import lrpc.common.RpcException;
import lrpc.common.codec.Decoder;
import lrpc.common.codec.Encoder;
import lrpc.common.protocol.RpcRequest;
import lrpc.util.ChannelGroup;
import lrpc.util.ChannelGroup.HealthChecker;
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
	private final ChannelGroup channelGroup;

	private final AtomicBoolean closed = new AtomicBoolean();

	public RpcClient(Endpoint endpoint) throws IOException {
		this(new RpcClientOptions(endpoint));
	}

	public RpcClient(RpcClientOptions options) throws IOException {
		this.options = options;
		this.workerGroup = new NioEventLoopGroup(options.getIoThreads());
		this.channelGroup = new ChannelGroup(options.getEndpoint(), createBootstrap(), options.getMaxConnections(),
				HealthChecker.ACTIVE);
	}

	private Bootstrap createBootstrap() {
		Endpoint endpoint = options.getEndpoint();
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.remoteAddress(endpoint.getIp(), endpoint.getPort());
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnectTimeoutMillis());
		b.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
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
		});
		return b;
	}

	public <T> T getProxy(Class<T> iface) throws RpcException {
		IProxyFactory proxyFactory = new JdkProxyFactory();
		return (T) proxyFactory.getProxy(new ClientInvoker<>(iface, this));
	}

	@SuppressWarnings("unchecked")
	<T> IFuture<T> send(Invocation inv) {
		RpcRequest request = new RpcRequest(inv);
		final long requestId = request.getId();
		final ResponseFuture future = new ResponseFuture(requestId, options.getRequestTimeoutMillis());
		try {
			Channel channel = channelGroup.getChannel(options.getConnectTimeoutMillis());
			channel.writeAndFlush(request).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					if (!f.isSuccess()) {
						ResponseFuture.doneWithException(requestId, f.cause());
					}
				}
			});
		} catch (Exception e) {
			ResponseFuture.doneWithException(requestId, e);
		}
		return (IFuture<T>) future;
	}
	public void close() {
		if (!closed.compareAndSet(false, true)) {
			return;
		}

		workerGroup.shutdownGracefully();
		channelGroup.close();
	}

	public final RpcClientOptions getOptions() {
		return options;
	}
}
