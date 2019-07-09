package io.hpsf.rpc.server;

import static io.hpsf.common.util.NettyUtils.writeAndFlush;

import io.hpsf.rpc.common.Invoker;
import io.hpsf.rpc.common.Invocation;
import io.hpsf.rpc.common.RpcResult;
import io.hpsf.rpc.common.ServerInfo;
import io.hpsf.rpc.common.protocol.HeartbeatMessage;
import io.hpsf.rpc.common.protocol.RpcMessage;
import io.hpsf.rpc.common.protocol.RpcRequest;
import io.hpsf.rpc.common.protocol.RpcResponse;
import io.hpsf.rpc.common.protocol.SyncMessage;
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
		} else {
			log.warn("Recieved unexpected message(type={}) on channel({})", req.getType(), ctx.channel());
			ctx.close();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 客户端连接后, 回写一个信息同步报文, 携带一些信息, 如心跳间隔等
		ServerInfo info = new ServerInfo();
		info.setHeartbeatIntervalMillis(rpcServer.getConfig().getHeartbeatInterval());
		ctx.writeAndFlush(new SyncMessage(info));
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
		log.debug("Recieved request message on channel({})", ctx.channel());
		rpcServer.getExecutor().execute(new InvocationTask(ctx.channel(), req));
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
			try {
				Invocation inv = request.getData();
				Class<?> clazz = Class.forName(inv.getClassName());
				Invoker<?> invoker = new ServerInvoker<>(clazz, rpcServer);
				Object result = invoker.invoke(inv);
				replyWithResult(ch, request.getId(), result);
			} catch (Throwable e) {
				log.error("Invocation({}) on channel({}) failed, cause: {}", request.getData(), ch, e.getMessage());
				replyWithException(ch, request.getId(), e);
			}
		}
	}
}
