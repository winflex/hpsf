package io.hpsf.rpc.consumer;

import static io.hpsf.rpc.consumer.RpcClient.HN_DECODER_HANDLER;
import static io.hpsf.rpc.consumer.RpcClient.HN_ENCODER_HANDLER;

import java.util.concurrent.TimeUnit;

import io.hpsf.common.ExtensionLoader;
import io.hpsf.common.concurrent.DefaultPromise;
import io.hpsf.common.util.NettyUtils;
import io.hpsf.rpc.RpcResult;
import io.hpsf.rpc.protocol.HandshakeRequest;
import io.hpsf.rpc.protocol.HandshakeResponse;
import io.hpsf.rpc.protocol.HeartbeatMessage;
import io.hpsf.rpc.protocol.RpcMessage;
import io.hpsf.rpc.protocol.RpcResponse;
import io.hpsf.rpc.protocol.codec.Decoder;
import io.hpsf.rpc.protocol.codec.Encoder;
import io.hpsf.serialization.api.Serializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author winflex
 */
@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<RpcMessage<?>> {

	public static final AttributeKey<DefaultPromise<HandshakeResponse>> ATTR_HANDSHAKE = AttributeKey.valueOf("ATTR_HANDSHAKE_PROMISE");
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<?> resp) throws Exception {
		if (resp instanceof RpcResponse) {
			handleResponse(ctx, (RpcResponse) resp);
		} else if (resp instanceof HandshakeRequest) {
			handleHandshake(ctx, (HandshakeRequest) resp);
		} else {
			log.warn("Recieved unexpected message(type={}) on channel({})", resp.getType(), ctx.channel());
		}
	}

	private void handleResponse(ChannelHandlerContext ctx, RpcResponse resp) {
		log.debug("Recieved response message on channel({})", ctx.channel());
		long requestId = resp.getId();
		RpcResult result = (RpcResult) resp.getData();
		if (result.isSuccess()) {
			ResponseFuture.doneWithResult(requestId, result.getResult());
		} else {
			ResponseFuture.doneWithException(requestId, result.getCause());
		}
	}

	private void handleHandshake(ChannelHandlerContext ctx, HandshakeRequest req) throws Exception {
		log.debug("Recieved handshake request message on channel({})", ctx.channel());
		HandshakeResponse resp = new HandshakeResponse();
		try {
			ChannelPipeline pl = ctx.pipeline();
			// 添加心跳处理器
			pl.addFirst(new IdleStateHandler(0, 0, req.getHeartbeatInterval(), TimeUnit.MILLISECONDS));
			// 为编解码器设置序列化处理器
			Serializer serializer = ExtensionLoader.getLoader(Serializer.class).getExtension(req.getSerializer());
			((Decoder) pl.get(HN_DECODER_HANDLER)).setSerializer(serializer);
			((Encoder) pl.get(HN_ENCODER_HANDLER)).setSerializer(serializer);
			resp.setSuccess(true);
		} catch (Throwable e) {
			log.error("Handle handshake request failed", e);
			resp.setSuccess(false);
		}
		NettyUtils.writeAndFlush(ctx.channel(), resp);
		ctx.channel().attr(ATTR_HANDSHAKE).get().setSuccess(resp);
	}
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(ATTR_HANDSHAKE).set(new DefaultPromise<>());
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			log.debug("Send heartbeat message on channel({})", ctx.channel());
			ctx.writeAndFlush(new HeartbeatMessage());
		}
	}
}
