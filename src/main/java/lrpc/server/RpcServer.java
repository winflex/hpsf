package lrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lrpc.common.RpcException;
import lrpc.common.codec.Decoder;
import lrpc.common.codec.Encoder;
import lrpc.util.concurrent.NamedThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author winflex
 */
public class RpcServer extends ServiceRepository {
	private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

	private final String ip;
	private final int port;
	private final int ioThreads;

	private final Executor defaultExecutor;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel serverChannel;

	public RpcServer(String ip, int port, int ioThreads) {
		this.ip = ip;
		this.port = port;
		this.ioThreads = ioThreads;
		this.defaultExecutor = new ThreadPoolExecutor(Runtime.getRuntime()
				.availableProcessors() * 2, Runtime.getRuntime()
				.availableProcessors() * 2, 1, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(
						"Rpc-Service-Exeecutor"));
	}

	public void start() throws RpcException {
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup(ioThreads);

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new ChannelInitializer<NioSocketChannel>() {

			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
				logger.info("channel connected, channel = {}", ch);
				ch.closeFuture().addListener(new ChannelFutureListener() {
					
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						logger.info("channel disconnected, channel = {}", ch);
					}
				});
				ChannelPipeline pl = ch.pipeline();
				pl.addLast(new Decoder());
				pl.addLast(new Encoder());
				pl.addLast(new RequestHandler(RpcServer.this));
			}
		});

		ChannelFuture f = null;
		if (ip == null || ip.isEmpty()) {
			f = b.bind(port).syncUninterruptibly();
		} else {
			f = b.bind(ip, port).syncUninterruptibly();
		}

		if (f.isSuccess()) {
			this.serverChannel = f.channel();
		} else {
			throw new RpcException(f.cause());
		}
	}

	public void detroy() {
		if (serverChannel != null) {
			serverChannel.close();
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
	}

	public final Executor getDefaultExecutor() {
		return defaultExecutor;
	}
}
