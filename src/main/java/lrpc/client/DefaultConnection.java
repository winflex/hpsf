package lrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import lrpc.common.codec.Decoder;
import lrpc.common.codec.Encoder;
import lrpc.util.concurrent.IFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO reconnect feature
 *
 * @author winflex
 */
public class DefaultConnection implements IConnection {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConnection.class);

	private final String ip;
	private final int port;

	private final EventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private volatile Channel channel;

	public DefaultConnection(String ip, int port, EventLoopGroup workerGroup) throws IOException {
		super();
		this.ip = ip;
		this.port = port;
		this.workerGroup = workerGroup;

		this.bootstrap = initBootstrap();
		this.channel = connect();
	}

	private Bootstrap initBootstrap() {
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<NioSocketChannel>() {

			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
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

	private Channel connect() throws IOException {
		ChannelFuture f = bootstrap.connect(ip, port).syncUninterruptibly();
		if (f.isSuccess()) {
			return f.channel();
		}
		Throwable cause = f.cause();
		if (cause instanceof IOException) {
			return (Channel) cause;
		} else {
			throw new IOException(cause);
		}
	}
	
	@Override
	public void close() throws Exception {
		channel.close().syncUninterruptibly();
	}

	@Override
	public IFuture<Object> send(Object req, final boolean needReply) {
		long requestId = sequence.getAndIncrement();
		final ResponseFuture future = new ResponseFuture(requestId);
		channel.writeAndFlush(req).addListener(new ChannelFutureListener() {
			
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
		return future;
	}

	/**
	 * request id generator
	 */
	private static final AtomicLong sequence = new AtomicLong();
}
