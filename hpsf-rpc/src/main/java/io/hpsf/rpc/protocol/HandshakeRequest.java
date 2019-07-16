package io.hpsf.rpc.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.hpsf.common.util.ExceptionUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author winflex
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HandshakeRequest extends RpcMessage<Void> {

	private static final long serialVersionUID = 1547128260024417442L;

	private int heartbeatInterval;
	private String serializer;

	public HandshakeRequest() {
		setType(TYPE_HANDSHAKE_REQUEST);
	}

	public byte[] encode() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(32);
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(heartbeatInterval);
			dos.writeShort(serializer.length());
			dos.writeBytes(serializer);
			return baos.toByteArray();
		} catch (IOException e) {
			ExceptionUtils.throwException(e);
		}
		return null; // never goes here
	}

	public void decode(byte[] bytes) {
		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
			heartbeatInterval = dis.readInt();

			int serializerBytesLen = dis.readShort();
			byte[] serializerBytes = new byte[serializerBytesLen];
			dis.read(serializerBytes, 0, serializerBytesLen);
			serializer = new String(serializerBytes);
		} catch (Throwable e) {
			ExceptionUtils.throwException(e);
		}
	}
}
