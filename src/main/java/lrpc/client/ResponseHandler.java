package lrpc.client;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lrpc.common.RpcResult;
import lrpc.common.protocol.HeartbeatMessage;
import lrpc.common.protocol.InitializeMessage;
import lrpc.common.protocol.RpcMessage;
import lrpc.common.protocol.RpcResponse;

/**
 * 
 *
 * @author winflex
 */
public class ResponseHandler extends SimpleChannelInboundHandler<RpcMessage<?>> {

	private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<?> resp) throws Exception {
		if (resp instanceof RpcResponse) {
			logger.debug("Recieved response message on channel({})", ctx.channel());
			long requestId = resp.getId();
			RpcResult result = (RpcResult) resp.getData();
			if (result.isSuccess()) {
				ResponseFuture.doneWithResult(requestId, result.getResult());
			} else {
				ResponseFuture.doneWithException(requestId, result.getCause());
			}
		} else if (resp instanceof InitializeMessage) {
			logger.debug("Recieved initialize message on channel({})", ctx.channel());
			InitializeMessage msg = (InitializeMessage) resp;
			ctx.channel().pipeline().addFirst(new IdleStateHandler(0, 0, msg.getData().getHeartbeatIntervalMillis(), TimeUnit.MILLISECONDS));
		} else {
			logger.warn("Recieved unexpected message(type={}) on channel({})", resp.getType(), ctx.channel());
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			logger.debug("Send heartbeat message on channel({})", ctx.channel());
			ctx.writeAndFlush(new HeartbeatMessage());
		}
	}
}
