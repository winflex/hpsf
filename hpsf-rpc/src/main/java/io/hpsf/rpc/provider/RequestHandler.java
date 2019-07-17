package io.hpsf.rpc.provider;

import static io.hpsf.common.util.NettyUtils.writeAndFlush;

import io.hpsf.registry.api.ServiceMeta;
import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.Invoker;
import io.hpsf.rpc.RpcException;
import io.hpsf.rpc.RpcResult;
import io.hpsf.rpc.protocol.HandshakeRequest;
import io.hpsf.rpc.protocol.HandshakeResponse;
import io.hpsf.rpc.protocol.HeartbeatMessage;
import io.hpsf.rpc.protocol.RpcMessage;
import io.hpsf.rpc.protocol.RpcRequest;
import io.hpsf.rpc.protocol.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author winflex
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RpcMessage<?>> {

	private final RpcServer rpcServer;

	private boolean handshaked;
	
	public RequestHandler(RpcServer rpcServer) {
		super();
		this.rpcServer = rpcServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<?> req) throws Exception {
		if (req instanceof RpcRequest) {
			// RPC调用
			handleInvocation(ctx, (RpcRequest) req);
		} else if (req instanceof HeartbeatMessage) {
			// 心跳
			handleHeartbeat(ctx, (HeartbeatMessage) req);
		} else if (req instanceof HandshakeResponse) {
			// 握手响应
			handleHandshakeResponse(ctx, (HandshakeResponse) req);
		} else {
			log.warn("Recieved unexpected message(type={}) on channel({})", req.getType(), ctx.channel());
			ctx.close();
		}
	}

	private void handleHandshakeResponse(ChannelHandlerContext ctx, HandshakeResponse resp) {
		log.debug("Recieved handshake response message on channel({})", ctx.channel());
		if (resp.isSuccess()) {
			handshaked = true;
		} else {
			log.error("Handshake failed, channel {} will be closed", ctx.channel());
			ctx.close();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 客户端连接后, 回写一个握手报文, 携带一些信息, 如心跳间隔等
		HandshakeRequest handshake = new HandshakeRequest();
		handshake.setHeartbeatInterval(rpcServer.getConfig().getHeartbeatInterval());
		handshake.setSerializer(rpcServer.getConfig().getSerializer());
		writeAndFlush(ctx.channel(), handshake);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			// 心跳超时, 关闭连接
			log.warn("Channel has passed idle timeout, channel = {}", ctx.channel());
			ctx.close();
		}
	}

	private void handleHeartbeat(ChannelHandlerContext ctx, HeartbeatMessage req) {
		log.debug("Recieved heartbeat message on channel({})", ctx.channel());
	}

	private void handleInvocation(ChannelHandlerContext ctx, RpcRequest req) {
		if (handshaked) {
			log.debug("Recieved request message on channel({})", ctx.channel());
			Invocation inv = req.getData();
			ServiceMeta meta = new ServiceMeta(inv.getClassName(), inv.getVersion());
			Publishment publishment = rpcServer.lookup(meta);
			if (publishment == null) {
				RpcException e = new RpcException(meta.directoryString() + " is not published");
				replyWithException(ctx.channel(), req.getId(), e);
				log.error("{}", e);
			} else {
				publishment.getExecutor().execute(new InvocationTask(ctx.channel(), req));
			}
		} else {
			log.error("Channel({}) not yet handshaked", ctx.channel());
			replyWithException(ctx.channel(), req.getId(), new Exception("Not yet handshaked"));
		}
	}

	/**
	 * rpc调用正常完成时调用此方法响应客户端
	 */
	private void replyWithResult(Channel ch, long id, Object data) {
		RpcResult result = new RpcResult();
		result.setResult(data);
		writeAndFlush(ch, new RpcResponse(id, result));
	}

	/**
	 * rpc调用异常完成时调用此方法响应客户端
	 */
	private ChannelFuture replyWithException(Channel ch, long id, Throwable cause) {
		RpcResult result = new RpcResult();
		result.setCause(cause);
		return writeAndFlush(ch, new RpcResponse(id, result));
	}

	final class InvocationTask implements Runnable {
		final Channel ch;
		final RpcRequest request;

		InvocationTask(Channel ch, RpcRequest request) {
			this.ch = ch;
			this.request = request;
		}

		@Override
		public void run() {
			Invocation inv = request.getData();
			Class<?> clazz = null;
			try {
				clazz = Class.forName(inv.getClassName());
			} catch (Throwable e) {
				log.error("Invocation({}) on channel({}) failed, cause: {}", request.getData(), ch, e.getMessage());
				replyWithException(ch, request.getId(), e);
				return;
			}

			Invoker<?> invoker = new ServerInvoker<>(clazz, rpcServer);
			try {
				Object result = invoker.invoke(inv);
				replyWithResult(ch, request.getId(), result);
			} catch (Throwable e) {
				replyWithException(ch, request.getId(), e);
			}
		}
	}
}
