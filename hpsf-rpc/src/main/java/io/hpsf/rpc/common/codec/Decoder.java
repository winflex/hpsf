package io.hpsf.rpc.common.codec;

import static io.hpsf.rpc.common.codec.CodecConstants.BODY_LENGTH_OFFSET;
import static io.hpsf.rpc.common.codec.CodecConstants.HEADER_LENGTH;
import static io.hpsf.rpc.common.codec.CodecConstants.MAGIC;
import static io.hpsf.rpc.common.protocol.RpcMessage.TYPE_HEARTBEAT;
import static io.hpsf.rpc.common.protocol.RpcMessage.TYPE_INVOKE_REQUEST;
import static io.hpsf.rpc.common.protocol.RpcMessage.TYPE_INVOKE_RESPONSE;
import static io.hpsf.rpc.common.protocol.RpcMessage.TYPE_SYNC;

import java.io.ByteArrayInputStream;
import java.util.List;

import io.hpsf.rpc.common.Invocation;
import io.hpsf.rpc.common.RpcResult;
import io.hpsf.rpc.common.ServerInfo;
import io.hpsf.rpc.common.protocol.HeartbeatMessage;
import io.hpsf.rpc.common.protocol.RpcMessage;
import io.hpsf.rpc.common.protocol.RpcRequest;
import io.hpsf.rpc.common.protocol.RpcResponse;
import io.hpsf.rpc.common.protocol.SyncMessage;
import io.hpsf.serialization.api.ISerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 2 bytes of magic
 * 1 bytes of type
 * 8 bytes of id
 * 4 bytes of data length
 * n bytes of data
 * 
 * @author winflex
 * 
 */
@Slf4j
public class Decoder extends ByteToMessageDecoder {

	private final ISerializer serializer;
	
	public Decoder(ISerializer serializer) {
		this.serializer = serializer;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();
		// 非整包不处理
		if (readableBytes < HEADER_LENGTH || readableBytes < (HEADER_LENGTH + in.getInt(BODY_LENGTH_OFFSET))) {
			return;
		}
		
		if (in.readShort() != MAGIC) {
			log.error("Recieved an unknown packet, the channel({}) will be closed", ctx.channel());
			return;
		}
		
		byte type = in.readByte();
		long id = in.readLong();
		int dataLength = in.readInt();
		byte[] dataBytes = null;
		if (dataLength > 0) {
			in.readBytes(dataBytes = new byte[dataLength]);
		}
		
		Object data = dataBytes;
		if (dataBytes != null && dataBytes.length > 0) {
			data = serializer.deserialize(new ByteArrayInputStream(dataBytes));
		}
		if (type == TYPE_INVOKE_REQUEST) {
			out.add(new RpcRequest(id, (Invocation) data));
		} else if (type == TYPE_INVOKE_RESPONSE) {
			out.add(new RpcResponse(id, (RpcResult) data));
		} else if (type == TYPE_HEARTBEAT) {
			out.add(new HeartbeatMessage());
		} else if (type == TYPE_SYNC) {
			out.add(new SyncMessage((ServerInfo) data));
		} else {
			RpcMessage<Object> rawMessage = new RpcMessage<>();
			rawMessage.setType(type);
			rawMessage.setId(id);
			rawMessage.setData(data);
			out.add(rawMessage);
		}
	}
}
