package io.hpsf.rpc.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author winflex
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SyncMessage extends RpcMessage<Object> {

	private static final long serialVersionUID = 1547128260024417442L;

	private int heartbeatInterval;
	private String serializer;
	
	public SyncMessage() {
		setType(TYPE_SYNC);
	}
	
	public void encode(ByteBuf buf) {
		buf.writeInt(heartbeatInterval);
		buf.writeShort(serializer.length());
		buf.writeBytes(serializer.getBytes()); // 假定是全英文不考虑乱码
	}
	
	public void decode(ByteBuf buf) {
		heartbeatInterval = buf.readInt();
		
		int serializerBytesLen = buf.readShort();
		byte[] serializerBytes = new byte[serializerBytesLen];
		buf.readBytes(serializerBytes);
		serializer = new String(serializerBytes);
	}
}
