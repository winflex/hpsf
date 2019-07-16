package io.hpsf.rpc.protocol.codec;
import static io.hpsf.rpc.protocol.RpcMessage.TYPE_HANDSHAKE_REQUEST;
import static io.hpsf.rpc.protocol.RpcMessage.TYPE_HANDSHAKE_RESPONSE;

import java.io.ByteArrayOutputStream;

import io.hpsf.rpc.protocol.HandshakeRequest;
import io.hpsf.rpc.protocol.HandshakeResponse;
import io.hpsf.rpc.protocol.RpcMessage;
import io.hpsf.serialization.api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 2 bytes of magic
 * 1 bytes of type 
 * 8 bytes of id 
 * 4 bytes of data length 
 * n bytes of data
 * 
 * @author winflex
 */
public class Encoder extends MessageToByteEncoder<RpcMessage<?>> {

	private Serializer serializer;

	public Encoder() {
	}

	public Encoder(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMessage<?> msg, ByteBuf out) throws Exception {
		out.writeShort(CodecConstants.MAGIC);
		out.writeByte(msg.getType());
		out.writeLong(msg.getId());

		if (msg.getType() == TYPE_HANDSHAKE_REQUEST) {
			byte[] data = ((HandshakeRequest) msg).encode();
			out.writeInt(data.length);
			out.writeBytes(data);
		} else if (msg.getType() == TYPE_HANDSHAKE_RESPONSE) {
			byte[] data = ((HandshakeResponse) msg).encode();
			out.writeInt(data.length);
			out.writeBytes(data);
		} else {
			if (msg.getData() == null) {
				out.writeInt(0);
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
				serializer.serialize(msg.getData(), baos);
				byte[] bytes = baos.toByteArray();
				out.writeInt(bytes.length);
				out.writeBytes(bytes);
			}
		}
	}

	public final void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
}
