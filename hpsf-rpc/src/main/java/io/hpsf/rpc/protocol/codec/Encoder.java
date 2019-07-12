package io.hpsf.rpc.protocol.codec;

import java.io.ByteArrayOutputStream;

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

	private final Serializer serializer;

	public Encoder(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMessage<?> msg, ByteBuf out) throws Exception {
		out.writeShort(CodecConstants.MAGIC);
		out.writeByte(msg.getType());
		out.writeLong(msg.getId());

		Object data = msg.getData();
		if (data == null) {
			out.writeInt(0);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
			serializer.serialize(data, baos);
			byte[] bytes = baos.toByteArray();
			out.writeInt(bytes.length);
			out.writeBytes(bytes);
		}
	}
}
