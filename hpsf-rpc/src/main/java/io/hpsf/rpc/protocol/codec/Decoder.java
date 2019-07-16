package io.hpsf.rpc.protocol.codec;

import static io.hpsf.rpc.protocol.RpcMessage.TYPE_HEARTBEAT;
import static io.hpsf.rpc.protocol.RpcMessage.TYPE_INVOKE_REQUEST;
import static io.hpsf.rpc.protocol.RpcMessage.TYPE_INVOKE_RESPONSE;
import static io.hpsf.rpc.protocol.RpcMessage.TYPE_HANDSHAKE_REQUEST;
import static io.hpsf.rpc.protocol.RpcMessage.TYPE_HANDSHAKE_RESPONSE;
import static io.hpsf.rpc.protocol.codec.CodecConstants.BODY_LENGTH_OFFSET;
import static io.hpsf.rpc.protocol.codec.CodecConstants.HEADER_LENGTH;
import static io.hpsf.rpc.protocol.codec.CodecConstants.MAGIC;

import java.io.ByteArrayInputStream;
import java.util.List;

import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.RpcResult;
import io.hpsf.rpc.protocol.HeartbeatMessage;
import io.hpsf.rpc.protocol.RpcMessage;
import io.hpsf.rpc.protocol.RpcRequest;
import io.hpsf.rpc.protocol.RpcResponse;
import io.hpsf.rpc.protocol.HandshakeRequest;
import io.hpsf.rpc.protocol.HandshakeResponse;
import io.hpsf.serialization.api.Serializer;
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

	private Serializer serializer;
	
	public Decoder() {
	}

	public Decoder(Serializer serializer) {
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
		if (type == TYPE_HANDSHAKE_REQUEST) {
			HandshakeRequest req = new HandshakeRequest();
			req.decode(dataBytes);
			out.add(req);
		} else if (type == TYPE_HANDSHAKE_RESPONSE) {
			HandshakeResponse resp = new HandshakeResponse();
			resp.decode(dataBytes);
			out.add(resp);
		} else {
			Object data = null;
			if (dataBytes != null && dataBytes.length > 0) {
				data = serializer.deserialize(new ByteArrayInputStream(dataBytes));
			}
			if (type == TYPE_INVOKE_REQUEST) {
				out.add(new RpcRequest(id, (Invocation) data));
			} else if (type == TYPE_INVOKE_RESPONSE) {
				out.add(new RpcResponse(id, (RpcResult) data));
			} else if (type == TYPE_HEARTBEAT) {
				out.add(new HeartbeatMessage());
			} else {
				RpcMessage<Object> rawMessage = new RpcMessage<>();
				rawMessage.setType(type);
				rawMessage.setId(id);
				rawMessage.setData(data);
				out.add(rawMessage);
			}
		}
	}

	public final void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
}
