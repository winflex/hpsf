package lrpc.server;

import static lrpc.util.NettyUtils.writeAndFlush;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lrpc.common.IInvoker;
import lrpc.common.Invocation;
import lrpc.common.RpcResult;
import lrpc.common.ServerInfo;
import lrpc.common.protocol.HeartbeatMessage;
import lrpc.common.protocol.InitializeMessage;
import lrpc.common.protocol.RpcMessage;
import lrpc.common.protocol.RpcRequest;
import lrpc.common.protocol.RpcResponse;

/**
 * 
 *
 * @author winflex
 */
public class RequestHandler extends SimpleChannelInboundHandler<RpcMessage<?>> {

	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

	private final RpcServer rpcServer;

	public RequestHandler(RpcServer rpcServer) {
		super();
		this.rpcServer = rpcServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<?> req) throws Exception {
		if (req instanceof RpcRequest) {
			handleInvocation(ctx, (RpcRequest) req);
		} else if (req instanceof HeartbeatMessage) {
			handleHeartbeat(ctx, (HeartbeatMessage) req);
		} else {
			logger.warn("Recieved unexpected message(type={}) on channel({})", req.getType(), ctx.channel());
			ctx.close();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ServerInfo info = new ServerInfo();
		info.setHeartbeatIntervalMillis(rpcServer.getOptions().getHeartbeatInterval());
		ctx.writeAndFlush(new InitializeMessage(info));
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			logger.warn("Channel has passed idle timeout, channel = {}", ctx.channel());
			ctx.close();
		}
	}

	private void handleHeartbeat(ChannelHandlerContext ctx, HeartbeatMessage req) {
		logger.debug("Recieved heartbeat message on channel({})", ctx.channel());
	}

	private void handleInvocation(ChannelHandlerContext ctx, RpcRequest req) {
		logger.debug("Recieved request message on channel({})", ctx.channel());
		rpcServer.getExecutor().execute(new InvocationTask(ctx.channel(), req));
	}

	/**
	 * reply to the channel with invocation result.
	 */
	private void replyWithResult(Channel ch, long id, Object data) {
		RpcResult result = new RpcResult();
		result.setResult(data);
		writeAndFlush(ch, new RpcResponse(id, result));
	}

	/**
	 * reply to the channel with exception.
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
				IInvoker<?> invoker = new ServerInvoker<>(clazz, rpcServer);
				Object result = invoker.invoke(inv);
				replyWithResult(ch, request.getId(), result);
			} catch (Throwable e) {
				logger.error("Invocation({}) on channel({}) failed, cause: {}", request.getData(), ch, e.getMessage());
				replyWithException(ch, request.getId(), e);
			}
		}
	}
}
