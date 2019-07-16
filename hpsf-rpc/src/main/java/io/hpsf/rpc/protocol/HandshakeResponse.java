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
public class HandshakeResponse extends RpcMessage<Void> {

	private static final long serialVersionUID = -5983297793771399452L;

	private boolean success;

	public HandshakeResponse() {
		setType(TYPE_HANDSHAKE_RESPONSE);
	}

	public byte[] encode() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(16);
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeBoolean(success);
			return baos.toByteArray();
		} catch (IOException e) {
			ExceptionUtils.throwException(e);
		}
		return null; // never goes here
	}

	public void decode(byte[] bytes) {
		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
			success = dis.readBoolean();
		} catch (Throwable e) {
			ExceptionUtils.throwException(e);
		}
	}
}
