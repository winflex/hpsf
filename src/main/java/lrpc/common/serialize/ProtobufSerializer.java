package lrpc.common.serialize;

import java.io.IOException;

import com.google.protobuf.Message;

/**
 * 
 * @author winflex
 */
public class ProtobufSerializer implements ISerializer {

	@Override
	public byte[] serialize(Object obj) throws IOException {
		if (!(obj instanceof Message)) {
			throw new RuntimeException(obj + " is not an instance of com.google.protobuf.Message");
		}
		Message msg = (Message) obj;
		byte[] clazz = msg.getClass().getName().getBytes();
		byte[] data = msg.toByteArray();
		
		byte[] full = new byte[2 + clazz.length + data.length];
		writeUngignedShort(full, 0, clazz.length);
		System.arraycopy(clazz, 0, full, 2, clazz.length);
		System.arraycopy(data, 0, full, 2 + clazz.length, data.length);
		return full;
	}

	@Override
	public <T> T deserialize(byte[] serializedData) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	static final void writeUngignedShort(byte[] b, int offset, int n) {
		b[0] = (byte) ((n >>> 8) & 0xff);
		b[1] = (byte) ((n >>> 0) & 0xff);
	}

	static final int readUnsignedShort(byte[] b, int offset) {
		int n0 = b[0] & 0xff;
		int n1 = b[1] & 0xff;
        return n0 << 8 | n1 << 0;
	}
	
	public static void main(String[] args) {
//		System.out.println(Short.MAX_VALUE);
		int n = Short.MAX_VALUE + 2;
		byte[] data = new byte[2];
		writeUngignedShort(data, 0, n);
		System.out.println(readUnsignedShort(data, 0));
	}
}
