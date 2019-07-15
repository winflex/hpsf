package io.hpsf.rpc.consumer;

import static io.hpsf.rpc.consumer.RpcClient.HN_RESPONSE_HANDLER;

import java.util.concurrent.TimeUnit;

import io.hpsf.common.ExtensionLoader;
import io.hpsf.rpc.RpcResult;
import io.hpsf.rpc.protocol.HeartbeatMessage;
import io.hpsf.rpc.protocol.RpcMessage;
import io.hpsf.rpc.protocol.RpcResponse;
import io.hpsf.rpc.protocol.SyncMessage;
import io.hpsf.rpc.protocol.codec.Decoder;
import io.hpsf.rpc.protocol.codec.Encoder;
import io.hpsf.serialization.api.Serializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author winflex
 */
@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<RpcMessage<?>> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<?> resp) throws Exception {
		if (resp instanceof RpcResponse) {
			log.debug("Recieved response message on channel({})", ctx.channel());
			long requestId = resp.getId();
			RpcResult result = (RpcResult) resp.getData();
			if (result.isSuccess()) {
				ResponseFuture.doneWithResult(requestId, result.getResult());
			} else {
				ResponseFuture.doneWithException(requestId, result.getCause());
			}
		} else if (resp instanceof SyncMessage) {
			log.debug("Recieved initialize message on channel({})", ctx.channel());
			SyncMessage msg = (SyncMessage) resp;
			ctx.channel().pipeline().addFirst(new IdleStateHandler(0, 0, msg.getHeartbeatInterval(), TimeUnit.MILLISECONDS));
			Serializer serializer = ExtensionLoader.getLoader(Serializer.class)
					.getExtension(msg.getSerializer());
			System.out.println("aa");
			ctx.pipeline().addBefore(HN_RESPONSE_HANDLER, "DECODER", new Decoder(serializer));
			ctx.pipeline().addBefore(HN_RESPONSE_HANDLER, "ENCODER", new Encoder(serializer));
		} else {
			log.warn("Recieved unexpected message(type={}) on channel({})", resp.getType(), ctx.channel());
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			log.debug("Send heartbeat message on channel({})", ctx.channel());
			ctx.writeAndFlush(new HeartbeatMessage());
		}
	}
}
