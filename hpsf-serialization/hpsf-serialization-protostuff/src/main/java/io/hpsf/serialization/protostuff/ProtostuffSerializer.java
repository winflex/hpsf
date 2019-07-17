package io.hpsf.serialization.protostuff;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import io.hpsf.serialization.api.SerializeException;
import io.hpsf.serialization.api.Serializer;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 
 * @author winflex
 */
public class ProtostuffSerializer implements Serializer {

	/**
	 * 每个线程缓存自己的buffer(protostuff扩容是新建一个buffer作为初始buffer的next, 因此我们这里实际上只缓存头节点)
	 */
	private static final ThreadLocal<LinkedBuffer> TL_BUFFER = new ThreadLocal<LinkedBuffer>() {

		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(512);
		};
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void serialize(Object obj, OutputStream out) throws SerializeException {
		LinkedBuffer buffer = TL_BUFFER.get();
		try {
			byte[] dataBytes;
			byte[] classNameBytes;
			if (needWrap(obj.getClass())) {
				Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
				dataBytes = GraphIOUtil.toByteArray(new Wrapper(obj), schema, buffer);
				classNameBytes = Wrapper.class.getName().getBytes();
			} else {
				Schema schema = RuntimeSchema.getSchema(obj.getClass());
				dataBytes = GraphIOUtil.toByteArray(obj, schema, buffer);
				classNameBytes = obj.getClass().getName().getBytes();
			}

			DataOutputStream dos = new DataOutputStream(out);
			dos.writeShort(classNameBytes.length); // short will be fine
			dos.write(classNameBytes);

			dos.writeInt(dataBytes.length);
			dos.write(dataBytes);
		} catch (IOException e) {
			throw new SerializeException(e);
		} finally {
			buffer.clear();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T deserialize(InputStream in) throws SerializeException {
		DataInputStream dis = new DataInputStream(in);
		try {
			byte[] classNameBytes = new byte[dis.readShort()];
			dis.readFully(classNameBytes);

			byte[] dataBytes = new byte[dis.readInt()];
			dis.readFully(dataBytes);

			Class<?> clazz = Class.forName(new String(classNameBytes));
			if (needWrap(clazz)) {
				Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
				Wrapper wrapper = schema.newMessage();
				GraphIOUtil.mergeFrom(dataBytes, wrapper, schema);
				return (T) wrapper.getData();
			} else {
				Schema schema = RuntimeSchema.getSchema(clazz);
				Object obj = schema.newMessage();
				GraphIOUtil.mergeFrom(dataBytes, obj, schema);
				return (T) obj;
			}
		} catch (Exception e) {
			throw new SerializeException(e);
		}
	}

	private static final Set<Class<?>> WRAPPER_SET = new HashSet<>();
	static {
		WRAPPER_SET.add(Map.class);
		WRAPPER_SET.add(HashMap.class);
		WRAPPER_SET.add(TreeMap.class);
		WRAPPER_SET.add(Hashtable.class);
		WRAPPER_SET.add(SortedMap.class);
		WRAPPER_SET.add(LinkedHashMap.class);
		WRAPPER_SET.add(ConcurrentHashMap.class);

		WRAPPER_SET.add(List.class);
		WRAPPER_SET.add(ArrayList.class);
		WRAPPER_SET.add(LinkedList.class);

		WRAPPER_SET.add(Vector.class);

		WRAPPER_SET.add(Set.class);
		WRAPPER_SET.add(HashSet.class);
		WRAPPER_SET.add(TreeSet.class);
		WRAPPER_SET.add(BitSet.class);

		WRAPPER_SET.add(StringBuffer.class);
		WRAPPER_SET.add(StringBuilder.class);

		WRAPPER_SET.add(BigDecimal.class);
		WRAPPER_SET.add(Date.class);
		WRAPPER_SET.add(Calendar.class);
		WRAPPER_SET.add(Time.class);

		WRAPPER_SET.add(Wrapper.class);

	}

	static boolean needWrap(Class<?> clazz) {
		return WRAPPER_SET.contains(clazz) || clazz.isArray() || clazz.isEnum();
	}
}
