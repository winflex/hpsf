package lrpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lrpc.common.protocol.RpcMessage;
import lrpc.common.serialize.KryoSerializer;

/**
 * 2 bytes of magic
 * 1 bytes of type
 * 8 bytes of id
 * 4 bytes of data length
 * n bytes of data
 * 
 * @author winflex
 */
public class Encoder extends MessageToByteEncoder<RpcMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
		out.writeShort(CodecConstants.MAGIC);
		out.writeByte(msg.getType());
		out.writeLong(msg.getId());
		
		Object data = msg.getData();
		if (data == null) {
			out.writeInt(0);
		} else {
			byte[] bytes = KryoSerializer.INSTANCE.serialize(data);
			out.writeInt(bytes.length);
			out.writeBytes(bytes);
		}
	}

}
