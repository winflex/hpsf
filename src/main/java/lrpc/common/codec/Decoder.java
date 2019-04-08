package lrpc.common.codec;

import static lrpc.common.codec.CodecConstants.BODY_LENGTH_OFFSET;
import static lrpc.common.codec.CodecConstants.HEADER_LENGTH;
import static lrpc.common.codec.CodecConstants.MAGIC;
import static lrpc.common.protocol.RpcMessage.TYPE_HEARTBEAT;
import static lrpc.common.protocol.RpcMessage.TYPE_INITIALIZE;
import static lrpc.common.protocol.RpcMessage.TYPE_INVOKE_REQUEST;
import static lrpc.common.protocol.RpcMessage.TYPE_INVOKE_RESPONSE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lrpc.common.Invocation;
import lrpc.common.RpcResult;
import lrpc.common.ServerInfo;
import lrpc.common.protocol.HeartbeatMessage;
import lrpc.common.protocol.InitializeMessage;
import lrpc.common.protocol.RpcMessage;
import lrpc.common.protocol.RpcRequest;
import lrpc.common.protocol.RpcResponse;
import lrpc.common.serialize.KryoSerializer;

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
		if (readableBytes < HEADER_LENGTH || readableBytes < (HEADER_LENGTH + in.getInt(BODY_LENGTH_OFFSET))) {
			return;
		}
		
		if (in.readShort() != MAGIC) {
			logger.error("Recieved an unknown packet, the channel({}) will be closed", ctx.channel());
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
			data = KryoSerializer.INSTANCE.deserialize(dataBytes);
		}
		if (type == TYPE_INVOKE_REQUEST) {
			out.add(new RpcRequest(id, (Invocation) data));
		} else if (type == TYPE_INVOKE_RESPONSE) {
			out.add(new RpcResponse(id, (RpcResult) data));
		} else if (type == TYPE_HEARTBEAT) {
			out.add(new HeartbeatMessage());
		} else if (type == TYPE_INITIALIZE) {
			out.add(new InitializeMessage((ServerInfo) data));
		} else {
			RpcMessage<Object> rawMessage = new RpcMessage<>();
			rawMessage.setType(type);
			rawMessage.setId(id);
			rawMessage.setData(data);
			out.add(rawMessage);
		}
	}

}
