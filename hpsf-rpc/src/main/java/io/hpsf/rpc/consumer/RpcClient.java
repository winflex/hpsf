/**
 * 
 */
package io.hpsf.rpc.consumer;

import static io.hpsf.common.util.NettyUtils.writeAndFlush;
import static io.hpsf.common.util.ExceptionUtils.throwException;

import io.hpsf.common.ExtensionLoader;
import io.hpsf.common.concurrent.Future;
import io.hpsf.common.concurrent.NamedThreadFactory;
import io.hpsf.registry.api.Registration;
import io.hpsf.registry.api.Registry;
import io.hpsf.registry.api.RegistryConfig;
import io.hpsf.registry.api.ServiceMeta;
import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.RpcException;
import io.hpsf.rpc.consumer.balance.LoadBalancerManager;
import io.hpsf.rpc.consumer.proxy.DefaultProxyFactory;
import io.hpsf.rpc.protocol.RpcRequest;
import io.hpsf.rpc.protocol.codec.Decoder;
import io.hpsf.rpc.protocol.codec.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC客户端实现
 * 
 * 
 * @author winflex
 */
@Slf4j
public class RpcClient {

	public static final String HN_RESPONSE_HANDLER = "RESPONSE_HANDLER";
	public static final String HN_ENCODER_HANDLER = "ENCODER_HANDLER";
	public static final String HN_DECODER_HANDLER = "DECODER_HANDLER";
	
	private final RpcClientConfig config; 
	private EventLoopGroup workerGroup; // effectively finaled
	private final ChannelManager channelManager;

	private final Registry registry;
	private final LoadBalancerManager loadBalancerManager = new LoadBalancerManager();

	public RpcClient(RpcClientConfig config) throws Exception {
		this.config = config;
		this.channelManager = new ChannelManager(createBootstrap(), config.getMaxConnectionPerServer());
		
		RegistryConfig registryConfig = RegistryConfig.parse(config.getRegistry());
		this.registry = ExtensionLoader.getLoader(Registry.class).getExtension(registryConfig.getType());
		this.registry.init(registryConfig.getConnectString());
	}

	private Bootstrap createBootstrap() {
		Bootstrap b = new Bootstrap();
		if (Epoll.isAvailable()) {
			workerGroup = new EpollEventLoopGroup(config.getIoThreads(), new NamedThreadFactory("hpsf-rpc-worker"));
			b.channel(EpollSocketChannel.class);
			b.option(EpollChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis());
		} else {
			workerGroup = new NioEventLoopGroup(config.getIoThreads(), new NamedThreadFactory("hpsf-rpc-worker"));
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis());
		}
		b.group(workerGroup);
		b.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				log.info("Channel connected, channel = {}", ch);
				ch.closeFuture().addListener((future) -> {
					log.info("Channel disconnected, channel = {}", ch);
				});

				ChannelPipeline pl = ch.pipeline();
				pl.addLast(new FlushConsolidationHandler(256, true));
				pl.addLast(HN_DECODER_HANDLER, new Decoder());
				pl.addLast(HN_ENCODER_HANDLER, new Encoder());
				pl.addLast(HN_RESPONSE_HANDLER, new ResponseHandler());
			}
		});
		return b;
	}

	public <T> T getServiceProxy(Class<T> iface, String serviceVersion) throws Exception {
		return (T) new DefaultProxyFactory().getProxy(new DefaultInvoker<>(iface, serviceVersion, this));
	}

	public GenericService getGenericServiceProxy(String iface, String serviceVersion) throws Exception {
		return new DefaultProxyFactory().getProxy(new GenericInvoker(iface, serviceVersion, this));
	}

	@SuppressWarnings("unchecked")
	<T> Future<T> send(Invocation inv) {
		RpcRequest request = new RpcRequest(inv);
		final long requestId = request.getId();
		final ResponseFuture future = new ResponseFuture(requestId, config.getRequestTimeoutMillis());
		try {
			ServiceMeta serviceMeta = new ServiceMeta(inv.getClassName(), inv.getVersion());
			Registration registration = loadBalancerManager.getLoadBalancer(serviceMeta).select(registry.lookup(serviceMeta));
			if (registration == null) {
				throwException(new RpcException(
						String.format("No providers found for %s-%s", inv.getClassName(), inv.getVersion())));
			}
			Channel channel = channelManager.getChannel(registration.getEndpoint(), config.getConnectTimeoutMillis());
			writeAndFlush(channel, request).addListener((f) -> {
				if (!f.isSuccess()) {
					ResponseFuture.doneWithException(requestId, f.cause());
				}
			});
		} catch (Exception e) {
			ResponseFuture.doneWithException(requestId, e);
		}
		return (Future<T>) future;
	}

	public void close() {
		registry.close();
		channelManager.close();
		workerGroup.shutdownGracefully();
		log.info("Rpc client closed");
	}

	public final RpcClientConfig getOptions() {
		return config;
	}
}
