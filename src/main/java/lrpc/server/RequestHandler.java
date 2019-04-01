package lrpc.server;

import static lrpc.common.protocol.RpcMessage.TYPE_HEARTBEAT_REQUEST;
import static lrpc.common.protocol.RpcMessage.TYPE_HEARTBEAT_RESPONSE;
import static lrpc.common.protocol.RpcMessage.TYPE_INVOKE_REQUEST;
import static lrpc.common.protocol.RpcMessage.TYPE_INVOKE_RESPONSE;
import static lrpc.util.NettyUtils.writeAndFlush;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lrpc.common.Invocation;
import lrpc.common.RpcException;
import lrpc.common.RpcResult;
import lrpc.common.protocol.RpcMessage;
import lrpc.server.IServiceRepository.Publishment;

/**
 * 
 *
 * @author winflex
 */
public class RequestHandler extends SimpleChannelInboundHandler<RpcMessage> {

	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

	private final RpcServer rpcServer;

	public RequestHandler(RpcServer rpcServer) {
		super();
		this.rpcServer = rpcServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage req) throws Exception {
		switch (req.getType()) {
		case TYPE_HEARTBEAT_REQUEST:
			handleHeartbeat(ctx, req);
			break;
		case TYPE_INVOKE_REQUEST:
			handleInvocation(ctx, req);
			break;
		default:
			logger.warn("Recieved unexpected message on channel({}), type = {}, channel will be closed", ctx.channel(), req.getType());
			ctx.close();
			break;
		}
	}

	private void handleHeartbeat(ChannelHandlerContext ctx, RpcMessage req) {
		logger.debug("Recieved heartbeat message on channel({})", ctx.channel());
		
		RpcMessage response = new RpcMessage(TYPE_HEARTBEAT_RESPONSE, req.getId(), null);
		writeAndFlush(ctx.channel(), response);
	}

	private void handleInvocation(ChannelHandlerContext ctx, RpcMessage req) {
		logger.debug("Recieved invocation message on channel({})", ctx.channel());
		
		Invocation inv = (Invocation) req.getData();
		Publishment publishment = rpcServer.get(inv.getClassName());
		if (publishment == null) {
			Throwable error = new RpcException(inv.getClassName() + "." + inv.getMethodName() + " is not published");
			replyWithException(ctx.channel(), req.getId(), error);
			logger.error("Invocation({}) on channel({}) failed, cause: not published", inv, ctx.channel());
			return;
		}

		Executor e = publishment.getExecutor() == null ? rpcServer.getDefaultExecutor() : publishment.getExecutor();
		e.execute(new InvocationTask(ctx.channel(), req, inv, publishment));
	}

	/**
	 * reply to the channel with invocation result.
	 */
	private void replyWithResult(Channel ch, long id, Object data) {
		RpcResult result = new RpcResult();
		result.setResult(data);
		writeAndFlush(ch, new RpcMessage(TYPE_INVOKE_RESPONSE, id, result));
	}
	
	/**
	 * reply to the channel with exception.
	 */
	private ChannelFuture replyWithException(Channel ch, long id, Throwable cause) {
		RpcResult result = new RpcResult();
		result.setCause(cause);
		return writeAndFlush(ch, new RpcMessage(TYPE_INVOKE_RESPONSE, id, result));
	}
	

	final class InvocationTask implements Runnable {
		final Channel ch;
		final RpcMessage request;
		final Invocation inv;
		final Publishment pub;

		InvocationTask(Channel ch, RpcMessage request, Invocation inv, Publishment publishment) {
			super();
			this.ch = ch;
			this.request = request;
			this.inv = inv;
			this.pub = publishment;
		}

		@Override
		public void run() {
			final Object instance = pub.getInstance(); // the service instance
			try {
				Method method = instance.getClass().getMethod(inv.getMethodName(), inv.getParameterTypes());
				if (method == null) {
					Throwable error = new Exception(inv.getClassName() + "." + inv.getMethodName()
							+ " is not published");
					replyWithException(ch, request.getId(), error);
					logger.error("Invocation({}) on channel({}) failed, cause: not published", inv, ch);
					return;
				}

				Object result = method.invoke(instance, inv.getParemeters());
				replyWithResult(ch, request.getId(), result);
			} catch (Throwable e) {
				logger.error("Invocation({}) on channel({}) failed, cause: {}", inv, ch, e.getMessage());
				replyWithException(ch, request.getId(), e);
			}
		}
	}
}
