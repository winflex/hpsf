package lrpc.common.codec;

import static lrpc.common.codec.CodecConstants.BODY_LENGTH_OFFSET;
import static lrpc.common.codec.CodecConstants.HEADER_LENGTH;
import static lrpc.common.codec.CodecConstants.MAGIC;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lrpc.common.protocol.RpcMessage;

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
public class Decoder extends ByteToMessageDecoder {

	private static final Logger logger = LoggerFactory.getLogger(Decoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();
		
		// not a full packet
		if (readableBytes < HEADER_LENGTH && readableBytes < HEADER_LENGTH + in.getInt(BODY_LENGTH_OFFSET)) {
			return;
		}
		
		if (in.readShort() != MAGIC) {
			logger.error("recieved an unknown packet, the channel {} will be closed", ctx.channel());
			return;
		}
		
		byte type = in.readByte();
		long id = in.readLong();
		int dataLength = in.readInt();
		byte[] data = null;
		if (dataLength > 0) {
			in.readBytes(data = new byte[dataLength]);
		}
		
		RpcMessage message = new RpcMessage(type, id, data);
		out.add(message);
	}

}
