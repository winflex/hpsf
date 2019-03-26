package lrpc.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lrpc.common.RpcException;

/**
 * 
 * @author winflex
 */
public class RpcServer extends ServicePublisher {

	private final String ip;
	private final int port;
	private final int ioThreads;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel serverChannel;

	public RpcServer(String ip, int port, int ioThreads) {
		this.ip = ip;
		this.port = port;
		this.ioThreads = ioThreads;
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
				ChannelPipeline pl = ch.pipeline();

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

	public static void main(String[] args) {
		Kryo kryo = new Kryo();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
		Output output = new Output(baos);
		kryo.writeClassAndObject(output, "a");
		output.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Input input = new Input(bais);
		Object o = kryo.readClassAndObject(input);
		input.close();

		System.out.println(o);
	}

}
