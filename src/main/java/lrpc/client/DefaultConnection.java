package lrpc.client;

import static lrpc.common.protocol.RpcMessage.TYPE_INVOKE_REQUEST;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import lrpc.common.codec.Decoder;
import lrpc.common.codec.Encoder;
import lrpc.common.protocol.RpcMessage;
import lrpc.util.Endpoint;
import lrpc.util.concurrent.IFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO reconnect feature
 *
 * @author winflex
 */
public class DefaultConnection implements RpcConnection {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConnection.class);

	private final Endpoint endpoint;
	private final long connectTimeoutMillis;
	private final EventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private volatile Channel channel;
	
	private volatile boolean closed;

	public DefaultConnection(Endpoint endpoint, EventLoopGroup workerGroup, long connectTimeoutMillis) throws IOException {
		this.endpoint = endpoint;
		this.workerGroup = workerGroup;
		this.connectTimeoutMillis = connectTimeoutMillis;

		this.bootstrap = initBootstrap();
		this.channel = connect();
	}

	private Bootstrap initBootstrap() {
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeoutMillis);
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
		ChannelFuture f = bootstrap.connect(endpoint.getIp(), endpoint.getPort()).syncUninterruptibly();
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
	public synchronized void close() throws Exception {
		if (!isClosed()) {
			channel.close().syncUninterruptibly();
		}
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public IFuture<Object> send(Object data, final boolean needReply) {
		long requestId = sequence.getAndIncrement();
		final ResponseFuture future = new ResponseFuture(requestId);
		RpcMessage request = new RpcMessage(TYPE_INVOKE_REQUEST, requestId, data);
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
		return future;
	}

	/**
	 * request id generator
	 */
	private static final AtomicLong sequence = new AtomicLong();
}
